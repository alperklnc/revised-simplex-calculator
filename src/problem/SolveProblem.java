package problem;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import calculations.*;
import calculations.SolveRS.HasNoOptimalSolutionException;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public class SolveProblem extends JFrame {

	private JPanel panel;

	private int winLength;
	private int winHeight;

	private JButton createNewProblemButton;

	private SolveRS result;

	public SolveProblem(int numOfVariable, int numOfConstraint, String problemType, double[][] cB_array,
			double[][] cN_array, double[][] N_array, double[][] b_array, double[][] B_inverse_array,
			String[] consEq_array, int twoPhaseChecker) throws HasNoOptimalSolutionException{
		
		setWinLength(Math.max(200 + 80 * numOfVariable, 450));
		setWinHeight(300 + 50 * numOfConstraint);
		
		result = new SolveRS(numOfVariable, numOfConstraint, problemType, cN_array, N_array, b_array, B_inverse_array, consEq_array, twoPhaseChecker);
		
		try {
			result.solve();
		} catch(HasNoOptimalSolutionException e) {
			panel = new JPanel();
			panel.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(panel);

			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, getWinLength(), getWinHeight());

			panel.setLayout(null);
			
			JLabel myLabel = new JLabel("Problem Has No Optimal Solution!");
			myLabel.setBounds((getWinLength() - 350) / 2, getWinHeight()/2 - 50, 350, 30);
			myLabel.setFont(new Font("Tahoma", Font.PLAIN, 22));
			panel.add(myLabel);
			
			createNewProblemButton = new JButton("Create New Problem");
			createNewProblemButton.setBounds((getWinLength() - 250) / 2, getWinHeight() - 110, 250, 40);
			createNewProblemButton.setFont(new Font("Tahoma", Font.PLAIN, 22));
			panel.add(createNewProblemButton);
		}
		
		initComponents();
		createEvents();
	}

	private void initComponents() {
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(panel);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, getWinLength(), getWinHeight());
		
		setTitle("Revised Simplex Calculator");
		
		panel.setLayout(null);
		
		if(result.hasOptimal()) {
			JLabel myLabel = new JLabel("Problem is solved!");
			myLabel.setBounds((getWinLength() - 200) / 2, 20, 200, 20);
			myLabel.setFont(new Font("Tahoma", Font.PLAIN, 22));
			panel.add(myLabel);
			
			JLabel info1 = new JLabel("!! Since all calculations were done as a \"double\" type,");
			JLabel info2 = new JLabel("there might some minor (.001%) calculation errors. !!");
			info1.setBounds((getWinLength() - 270) / 2, 40, 270, 20);
			info2.setBounds((getWinLength() - 270) / 2, 55, 270, 20);
			info1.setFont(new Font("Tahoma", Font.PLAIN, 11));
			info2.setFont(new Font("Tahoma", Font.PLAIN, 11));
			panel.add(info1);
			panel.add(info2);
			
			displayOptimalSolution();
			
			JLabel info3 = new JLabel("You can see your entire solution on the console, step-by-step");
			info3.setBounds((getWinLength() - 390) / 2, getWinHeight() - 140, 390, 20);
			info3.setFont(new Font("Tahoma", Font.PLAIN, 14));
			panel.add(info3);
		}
		else {
			JLabel myLabel = new JLabel("Problem Has No Optimal Solution!");
			myLabel.setBounds((getWinLength() - 350) / 2, getWinHeight()/2 - 50, 350, 30);
			myLabel.setFont(new Font("Tahoma", Font.PLAIN, 22));
			panel.add(myLabel);
		}

		createNewProblemButton = new JButton("Create New Problem");
		createNewProblemButton.setBounds((getWinLength() - 250) / 2, getWinHeight() - 110, 250, 40);
		createNewProblemButton.setFont(new Font("Tahoma", Font.PLAIN, 22));
		panel.add(createNewProblemButton);

	}
	
	private void displayOptimalSolution() {
		displayBasic();
		displayxB();
		displayz();
	}

	private void displayBasic() {
		String basicStr = "x* = {";
		for (String str : result.getBasicVariables()) {
			basicStr += str + ",  ";
		}
		basicStr = basicStr.substring(0, basicStr.length()-3);
		basicStr += "}";
		
		JLabel basicVariableXLabel = new JLabel(basicStr);
		basicVariableXLabel.setBounds(10, 90, 50 + 50 * result.getNumOfConstraint(), 20);
		basicVariableXLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel.add(basicVariableXLabel);
	}


	private void displayxB() {
		JLabel xBLabel = new JLabel("xB =");
		xBLabel.setBounds(10, 90 + 25 * result.getNumOfConstraint(), 100, 15);
		xBLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel.add(xBLabel);

		String xBStr = "";
		int count = 0;
		double[][] tmp_array = result.getxB().getArray();
		for (double[] row : tmp_array) {
			JLabel newLabel = new JLabel();
			for (double column : row) {
				xBStr += column + "   ";
				newLabel = new JLabel(xBStr);
				newLabel.setBounds(60, 90 + 25 * result.getNumOfConstraint() + count * 25,
						150 + 60 * result.getNumOfVariable(), 15);
				newLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
			}
			xBStr = "";
			panel.add(newLabel);
			count++;
		}
	}

	private void displayz() {
		String str = "";
		double[][] tmp_array1 = result.getZ().getArray();
		for (double[] row : tmp_array1) {
			for (double column : row) {
				str += column;
			}
		}
		JLabel zLabel = new JLabel("z = " + str);
		zLabel.setBounds(10, 100 + 50 * result.getNumOfConstraint(), 150, 16);
		zLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel.add(zLabel);
	}

	private void createEvents() {
		createNewProblemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Input input = new Input();
					input.setVisible(true);
					dispose();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, JOptionPane.ERROR_MESSAGE);
				}
			}
		});

	}

	private int getWinLength() {
		return winLength;
	}

	private void setWinLength(int winLength) {
		this.winLength = winLength;
	}

	private int getWinHeight() {
		return winHeight;
	}

	private void setWinHeight(int winHeight) {
		this.winHeight = winHeight;
	}
}
