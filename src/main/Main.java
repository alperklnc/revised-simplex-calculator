package main;

import java.awt.EventQueue;

import calculations.SolveRS;
import problem.Input;


/*
 * Revised Simplex Calculator
 * INDR 262 - 2020 Spring - Bonus Project
 * Instructor - Metin Türkay
 * 
 * @author - Alper Kılınç - 63912
 * 
 * This program calculates LP problems using Revised Simplex Method with Graphical Interface.
 * User may enter variables by hand from directly source.
 * Please read below to how to do that.
 * 
 */

public class Main {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					/////////////////////////////////////////////////// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
					////////////////////								   							   \\\\\\\\\\\\\\\\\\\\
					//////////////////// IF YOU WANT TO ENTER LARGE NUMBER OF VARIABLES OR CONSTRAINTS \\\\\\\\\\\\\\\\\\\\
					////////////////////								   						       \\\\\\\\\\\\\\\\\\\\
					/////////////////////////////////////////////////// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
					
					/*
					 * First, you should "comment" following two lines in order to disable GUI.
					 */
					Input input = new Input();
					input.setVisible(true);
					
					/*
					 * Then, you should enter your variables by hand.
					 * Please see the example
					 * 
					 */
					
					/*
					 EXAMPLE
					 First, enter your problem type: Max or Min
					 String problemType = "max"; 
					 
					 Second, enter your coefficients for each constraint.
					 !! BE CAREFUL !! This is a 3 * 2 Matrix.
					 double[][] N_Array = {{1, 0},
					 					   {0, 2},
					                       {3, 2}};
					
					 Third, enter your b;
					 double[][] b_Array = {{4},
					 					   {12},
					 					   {18}};
					 
					 Then enter your non-basic variable's coefficients
					 double[][] cN_Array = {{3, 5}};
					 
					 Finally, enter your equalities for each constraint, IN ORDER.
					 String[] consEqualities = {"<=", "<=", "<="};
					 
					 Call the SolveRS function by using field witch you created.
					 SolveRS revised = new SolveRS(problemType, cN_Array, N_Array, b_Array, consEqualities);
					 revised.solve();
					 
					 You can see your solution step by step in the console.
					*/
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
