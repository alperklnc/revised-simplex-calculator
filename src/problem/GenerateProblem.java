package problem;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import calculations.TwoPhase;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class GenerateProblem extends JFrame {

	private JPanel panel;
	private int numOfVariable;
	private int numOfConstraint;

	private JComboBox<String> maxMinCB;

	private List<JTextField> cNListOfTF = new ArrayList<JTextField>();
	private double[][] cB_array;

	private List<JTextField> xListOfTF = new ArrayList<JTextField>();
	private double[][] N_array;

	private List<JTextField> bListOfTF = new ArrayList<JTextField>();
	private double[][] b_array;

	private double[][] cN_array;
	private double[][] B_inverse_array;

	private List<JComboBox<String>> consEqListOfCB = new ArrayList<JComboBox<String>>();
	private String[] consEq_array;

	private JButton solveButton;
	private JButton createNewProblemButton;

	private int winLength;
	private int winHeight;

	private static final Font middleFont = new Font("Tahoma", Font.PLAIN, 22);
	private static final Font smallFont = new Font("Tahoma", Font.PLAIN, 16);

	public GenerateProblem(int numOfVariable, int numOfConstraint) {
		this.numOfVariable = numOfVariable;
		this.numOfConstraint = numOfConstraint;

		N_array = new double[numOfConstraint][numOfVariable];

		B_inverse_array = new double[numOfConstraint][numOfConstraint];

		b_array = new double[numOfConstraint][1];

		cN_array = new double[1][numOfVariable];

		cB_array = new double[1][numOfConstraint];
		for (int i = 0; i < numOfConstraint; i++) {
			cB_array[0][i] = 0.0;
		}

		consEq_array = new String[numOfConstraint];

		initComponents(numOfVariable, numOfConstraint);
		createEvents();
	}

	private void initComponents(int numOfVariable, int numOfConstraint) {
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(panel);

		setTitle("Revised Simplex Calculator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setWinLength(Math.max(200 + 80 * numOfVariable, 300));
		setWinHeight(300 + 40 * numOfConstraint);
		setBounds(400, 200, getWinLength(), getWinHeight());

		panel.setLayout(null);

		objectiveFunction(numOfVariable);
		constraints(numOfConstraint);

		solveButton = new JButton("Solve Problem");
		solveButton.setBounds((winLength - 250) / 2, winHeight - 160, 250, 40);
		panel.add(solveButton);
		solveButton.setFont(middleFont);

		createNewProblemButton = new JButton("Create New Problem");
		createNewProblemButton.setBounds((winLength - 250) / 2, winHeight - 100, 250, 40);
		panel.add(createNewProblemButton);
		createNewProblemButton.setFont(middleFont);
	}

	private void objectiveFunction(int numOfVariable) {
		JPanel objectiveFunctionPanel = new JPanel();
		objectiveFunctionPanel.setBounds(50, 50, 100 + 80 * numOfVariable, 30);
		panel.add(objectiveFunctionPanel);

		maxMinCB = new JComboBox<String>();
		maxMinCB.setBounds(0, 0, 60, 30);
		maxMinCB.addItem("max");
		maxMinCB.addItem("min");
		objectiveFunctionPanel.setLayout(null);

		maxMinCB.setFont(smallFont);
		objectiveFunctionPanel.add(maxMinCB);

		JLabel lblZ = new JLabel("z =");
		lblZ.setBounds(70, 0, 30, 30);
		objectiveFunctionPanel.add(lblZ);
		lblZ.setFont(smallFont);

		for (int i = 1; i <= numOfVariable; i++) {
			JTextField c = new JTextField();
			cNListOfTF.add(c);
			c.setBounds(100 + (i - 1) * 80, 0, 30, 30);
			
			c.addFocusListener(new java.awt.event.FocusAdapter() {
			    public void focusGained(java.awt.event.FocusEvent evt) {
			        SwingUtilities.invokeLater(new Runnable() {
			            @Override
			            public void run() {
			                c.selectAll();
			            }
			        });
			    }
			});
			
			objectiveFunctionPanel.add(c);

			JLabel lblX = new JLabel("x" + i);
			lblX.setBounds(135 + (i - 1) * 80, 0, 40, 30);
			lblX.setFont(smallFont);
			objectiveFunctionPanel.add(lblX);

			if (i != numOfVariable) {
				JLabel lblSign = new JLabel("+");
				lblSign.setBounds(165 + (i - 1) * 80, 0, 20, 30);
				lblSign.setFont(smallFont);
				objectiveFunctionPanel.add(lblSign);
			}
		}
	}

	private void constraints(int numOfConstraint) {
		JLabel lblSubjectTo = new JLabel("Subject to");
		lblSubjectTo.setBounds(50, 90, 80, 20);
		lblSubjectTo.setFont(smallFont);
		panel.add(lblSubjectTo);

		for (int i = 1; i <= numOfConstraint; i++) {
			createConstraint(getNumOfVariable(), i);
		}
	}

	private void createConstraint(int numOfVariable, int index) {
		JPanel constraintPanel = new JPanel();
		constraintPanel.setBounds(50, 120 + (index - 1) * 40, 250 + numOfVariable * 80, 30);
		panel.add(constraintPanel);
		constraintPanel.setLayout(null);

		for (int i = 1; i <= numOfVariable; i++) {
			JTextField x = new JTextField();
			xListOfTF.add(x);
			x.setBounds(0 + (i - 1) * 80, 0, 30, 30);
			constraintPanel.add(x);
			
			x.addFocusListener(new java.awt.event.FocusAdapter() {
			    public void focusGained(java.awt.event.FocusEvent evt) {
			        SwingUtilities.invokeLater(new Runnable() {
			            @Override
			            public void run() {
			                x.selectAll();
			            }
			        });
			    }
			});
			
			JLabel lblX = new JLabel("x" + i);
			lblX.setBounds(35 + (i - 1) * 80, 0, 30, 30);
			lblX.setFont(smallFont);
			constraintPanel.add(lblX);

			if (i != numOfVariable) {
				JLabel lblSign = new JLabel("+");
				lblSign.setBounds(65 + (i - 1) * 80, 0, 15, 30);
				lblSign.setFont(smallFont);
				constraintPanel.add(lblSign);
			}
		}

		JComboBox<String> constraintEquality = new JComboBox<String>();
		consEqListOfCB.add(constraintEquality);
		constraintEquality.setBounds(70 + (numOfVariable - 1) * 80, 0, 50, 30);
		constraintEquality.setFont(smallFont);
		constraintEquality.addItem("<=");
		constraintEquality.addItem("=");
		constraintEquality.addItem(">=");
		constraintPanel.add(constraintEquality);

		JTextField b = new JTextField();
		bListOfTF.add(b);
		b.setBounds(130 + (numOfVariable - 1) * 80, 0, 40, 30);
		constraintPanel.add(b);
		
		b.addFocusListener(new java.awt.event.FocusAdapter() {
		    public void focusGained(java.awt.event.FocusEvent evt) {
		        SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
		                b.selectAll();
		            }
		        });
		    }
		});
	}

	private void createEvents() {
		solveButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				try {
					for (int i = 0; i < cNListOfTF.size(); i++) {
						cN_array[0][i] = Double.parseDouble(cNListOfTF.get(i).getText());
					}

					for (int i = 0; i < bListOfTF.size(); i++) {
						b_array[i][0] = Double.parseDouble(bListOfTF.get(i).getText());
					}

					for (int i = 0; i < getNumOfConstraint(); i++) {
						for (int j = 0; j < getNumOfVariable(); j++) {
							N_array[i][j] = Double.parseDouble(xListOfTF.get(i * getNumOfVariable() + j).getText());
						}
					}

					for (int i = 0; i < consEqListOfCB.size(); i++) {
						consEq_array[i] = (String) consEqListOfCB.get(i).getSelectedItem();
					}
				} catch(NumberFormatException e) {
					JOptionPane.showMessageDialog(null,"Please enter a NUMBER!"
							+ "\nDO NOT leave any field empty. If there is no coefficent, enter \"0\""
							+ "\nEnter double numbers in the \"X.XX\" format", "Invalid Type!", JOptionPane.ERROR_MESSAGE);
				}
				
				SolveProblem solveProblem;
				int twoPhaseChecker = 0; // Two Phase is required, if 1; if not, 0. 
				for(String str : consEq_array) {
					if(str.equals(">=") && str.equals("=")) {
						twoPhaseChecker = 1;
					} 
					else if(str.equals(">=")) {
						twoPhaseChecker = 1;
					}
					else if(str.equals("=")) {
						twoPhaseChecker = 2;
					}
				}
				if(twoPhaseChecker == 0) {
					solveProblem = new SolveProblem(getNumOfVariable(), getNumOfConstraint(),
							(String) maxMinCB.getSelectedItem(), cB_array, cN_array, N_array, b_array, B_inverse_array,
							consEq_array, twoPhaseChecker);
					solveProblem.setVisible(true);
				} else if(twoPhaseChecker == 1) {
					System.out.println("Switching to 2 Phase...");
					TwoPhase twoPhase = new TwoPhase(getNumOfVariable(), getNumOfConstraint(), cN_array, N_array, b_array, B_inverse_array, consEq_array);
					solveProblem = new SolveProblem(getNumOfVariable(), getNumOfConstraint(),
							(String) maxMinCB.getSelectedItem(), cB_array, cN_array, twoPhase.getN_array(), twoPhase.getB_array(), twoPhase.getBinverse_array(),
							consEq_array, twoPhaseChecker);
					solveProblem.setVisible(true);
				} else if(twoPhaseChecker == 2) {
					solveProblem = new SolveProblem(getNumOfVariable(), getNumOfConstraint(),
							(String) maxMinCB.getSelectedItem(), cB_array, cN_array, N_array, b_array, B_inverse_array,
							consEq_array, twoPhaseChecker);
					solveProblem.setVisible(true);
				}
				dispose();
			}
		});
		
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

	public int getNumOfVariable() {
		return numOfVariable;
	}

	public void setNumOfVariable(int numOfVariable) {
		this.numOfVariable = numOfVariable;
	}

	public int getNumOfConstraint() {
		return numOfConstraint;
	}

	public void setNumOfConstraint(int numOfConstraint) {
		this.numOfConstraint = numOfConstraint;
	}

	public int getWinLength() {
		return winLength;
	}

	public void setWinLength(int winLength) {
		this.winLength = winLength;
	}

	public int getWinHeight() {
		return winHeight;
	}

	public void setWinHeight(int winHeight) {
		this.winHeight = winHeight;
	}
}
