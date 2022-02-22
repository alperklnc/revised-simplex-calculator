package problem;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Input extends JFrame {

	private static int numOfVariable;
	private static int numOfConstraint;
	
	private JPanel welcomePanel;
	private JTextField variableTF;
	private JTextField constrainTF;
	private JButton generateButton;
	private JPanel panel;

	private static final Font small = new Font("Tahoma", Font.PLAIN, 16);
	private static final Font middle = new Font("Tahoma", Font.PLAIN, 22);
	private static final Font large = new Font("Tahoma", Font.PLAIN, 30);

	private boolean isClosed = false;

	public Input() {
		initComponents();
		createEvents();
	}

	private void initComponents() {
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(panel);

		setTitle("Revised Simplex Calculator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 200, 750, 500);

		panel.setLayout(null);

		welcome();
		createValuesPanel();

		generateButton = new JButton("Generate Problem");
		generateButton.setBounds(241, 360, 250, 40);
		panel.add(generateButton);
		generateButton.setFont(middle);
	}

	private void welcome() {
		welcomePanel = new JPanel();
		welcomePanel.setBounds(12, 50, 700, 150);
		panel.add(welcomePanel);

		JLabel welcome = new JLabel("Welcome to Revised Simplex Calculator!");
		welcome.setFont(large);

		JLabel info1 = new JLabel("This program solves LP problems with Revised Simplex Method.");
		info1.setFont(small);

		JLabel info2 = new JLabel("First, you should enter the number of variables and constrains.");
		info2.setFont(small);

		JLabel info3 = new JLabel("Then, hit the \"Generate Problem\" button.");
		info3.setFont(small);

		GroupLayout gl_welcomePanel = new GroupLayout(welcomePanel);
		gl_welcomePanel.setHorizontalGroup(gl_welcomePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_welcomePanel.createSequentialGroup().addGroup(gl_welcomePanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_welcomePanel.createSequentialGroup().addGap(82).addComponent(welcome,
								GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(gl_welcomePanel.createSequentialGroup().addGap(138).addComponent(info1))
						.addGroup(gl_welcomePanel.createSequentialGroup().addGap(126)
								.addComponent(info2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addGap(44))
						.addGroup(
								gl_welcomePanel
										.createSequentialGroup().addGap(203).addComponent(info3,
												GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addGap(120)))
						.addGap(83)));
		gl_welcomePanel.setVerticalGroup(gl_welcomePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_welcomePanel.createSequentialGroup().addGap(5).addComponent(welcome).addGap(30)
						.addComponent(info1).addPreferredGap(ComponentPlacement.RELATED).addComponent(info2)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(info3).addGap(44)));
		welcomePanel.setLayout(gl_welcomePanel);
	}

	private void createValuesPanel() {
		JPanel valuesPanel = new JPanel();
		valuesPanel.setBounds(141, 250, 450, 60);
		panel.add(valuesPanel);
		valuesPanel.setLayout(new GridLayout(0, 2, 0, 10));

		JLabel variable_text = new JLabel("Number of Variables:");
		valuesPanel.add(variable_text);
		variable_text.setFont(middle);

		variableTF = new JTextField();
		variableTF.setHorizontalAlignment(SwingConstants.LEFT);
		valuesPanel.add(variableTF);
		variableTF.setColumns(10);
		
		variableTF.addFocusListener(new java.awt.event.FocusAdapter() {
		    public void focusGained(java.awt.event.FocusEvent evt) {
		        SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
		            	variableTF.selectAll();
		            }
		        });
		    }
		});

		JLabel constrain_text = new JLabel("Number of Constrains:");
		valuesPanel.add(constrain_text);
		constrain_text.setFont(middle);

		constrainTF = new JTextField();
		valuesPanel.add(constrainTF);
		constrainTF.setColumns(10);
		
		constrainTF.addFocusListener(new java.awt.event.FocusAdapter() {
		    public void focusGained(java.awt.event.FocusEvent evt) {
		        SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
		            	constrainTF.selectAll();
		            }
		        });
		    }
		});
	}

	private void createEvents() {
		generateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					setNumOfVariable(Integer.parseInt(variableTF.getText()));
					setNumOfConstraint(Integer.parseInt(constrainTF.getText()));
					if (numOfVariable <= 0 || numOfConstraint <= 0) {
						JOptionPane.showMessageDialog(null,
								"Non-positive values are not valid!\n" + "Please enter a POSITIVE number.",
								"Non-Positive Value!", JOptionPane.ERROR_MESSAGE);
					} else if(numOfVariable >= 20) {
						int n = JOptionPane.showConfirmDialog(null, "You entered too much \"Variable\"!"
								+ "\nDepends on your screen size, these number of variable fields may not be seen on the screen completely."
								+ "\nIf you want to enter large number of variables or constraints, you may enter them from source code by hand "
								+ "(for the sake of the visualization.)"
								+ "\nYou can find an explanation about how to do that in the \"Main\" class of the source code."
								+ "\nDo you want to continue with these numbers?", "Warning!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						if(n == JOptionPane.YES_OPTION) {
							GenerateProblem problem = new GenerateProblem(getNumOfVariable(), getNumOfConstraint());
							problem.setVisible(true);
							dispose();
						}
						else if(n == JOptionPane.NO_OPTION) {
							
						}
					} else if(numOfConstraint >= 17) {
						int n = JOptionPane.showConfirmDialog(null, "You entered too much \"Constraint\"!"
								+ "\nDepends on your screen size, these number of constraint fields may not be seen on the screen completely."
								+ "\nIf you want to enter large number of variables or constraints, you may enter them from source code by hand "
								+ "(for the sake of the visualization.)"
								+ "\nYou can find an explanation about how to do that in the \"Main\" class of the source code."
								+ "\nDo you want to continue with these numbers?", "Warning!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						
						if(n == JOptionPane.YES_OPTION) {
							GenerateProblem problem = new GenerateProblem(getNumOfVariable(), getNumOfConstraint());
							problem.setVisible(true);
							dispose();
						}
						else if(n == JOptionPane.CANCEL_OPTION) {
							
						}
					} 
					else {
						GenerateProblem problem = new GenerateProblem(getNumOfVariable(), getNumOfConstraint());
						problem.setVisible(true);
						dispose();
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Please enter an INTEGER.", "Invalid Type!", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	public int getNumOfVariable() {
		return numOfVariable;
	}

	public static void setNumOfVariable(int numOfVariable) {
		Input.numOfVariable = numOfVariable;
	}

	public int getNumOfConstraint() {
		return numOfConstraint;
	}

	public static void setNumOfConstraint(int numOfConstraint) {
		Input.numOfConstraint = numOfConstraint;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

}
