package calculations;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SolveRS {

	private String objectiveFunction;
	private String problemType; // max or min
	private String constrains;

	private int numOfVariable;
	private int numOfConstraint;

	private Matrix N;
	private Matrix Binverse;
	private Matrix b; // n x 1

	private String[] basicVariables;
	private Matrix cB; // 1 x n

	private String[] nonBasicVariables;
	private Matrix cN; // 1 x n

	private Matrix xB; // n x 1
	private Matrix z;

	private Matrix w; // 1 x n
	private Matrix E;
	private Matrix eta; // n x 1

	private Matrix step_two;

	private String EBV;
	private int EBV_INDEX;
	private Matrix EBV_COLUMN;

	private String LBV;
	private int LBV_INDEX;
	private Matrix LBV_COLUMN;

	private double[][] I;

	private boolean isOptimal = false;
	private boolean hasOptimal = true;
	
	private String[] consEq_array;
	
	public SolveRS(String problemType, double[][] cN_array, double[][] N_array, double[][] b_array, String[] consEq_array) {
		setNumOfVariable(new Matrix(N_array).getNumberOfColumns());
		setNumOfConstraint(new Matrix(N_array).getNumberOfRows());
		
		this.problemType = problemType;
		this.consEq_array = consEq_array;
		
		setcN(new Matrix(cN_array));
		setN(new Matrix(N_array));
		setB(new Matrix(b_array));
		

		
		setInitials();
	}
	
	public SolveRS(int numOfVariable, int numOfConstraint, String problemType, double[][] cN_array,
			double[][] N_array, double[][] b_array, double[][] Binverse_array, String[] consEq_array, int twoPhaseChecker) {

		this.numOfVariable = numOfVariable;
		this.numOfConstraint = numOfConstraint;
		this.consEq_array  = consEq_array;
		this.problemType = problemType;
		
		
		setcN(new Matrix(cN_array));
		setN(new Matrix(N_array));
		setB(new Matrix(b_array));
		
		setInitials();
		if(twoPhaseChecker == 1) {
			setBinverse(new Matrix(Binverse_array));
		} else if(twoPhaseChecker == 0) {
			double[][] initBinverse = new double[getNumOfConstraint()][getNumOfConstraint()];
			String[] tmp = getConsEq_array();
			for(int i = 0, j = 0; i < getNumOfConstraint(); i++, j++) {
				if(tmp[i].equals("<=")) {
					initBinverse[i][j] = 1;
				} else if(tmp[i].equals(">=")) {
					initBinverse[i][j] = -1;
				} else if(tmp[i].equals("=")) {
					initBinverse[i][j] = 0.0;
				} 
			}
			setBinverse(new Matrix(initBinverse));
		} else if(twoPhaseChecker == 2) {
			double[][] initBinverse = new double[getNumOfConstraint()][getNumOfConstraint()];
			String[] tmp = getConsEq_array();
			for(int i = 0, j = 0; i < getNumOfConstraint(); i++, j++) {
				if(tmp[i].equals("<=")) {
					initBinverse[i][j] = 1;
				} else if(tmp[i].equals(">=")) {
					initBinverse[i][j] = -1;
				} else if(tmp[i].equals("=")) {
					initBinverse[i][j] = 1;
				} 
			}
			setBinverse(new Matrix(initBinverse));
		}
	}
	
	private void setInitials() {
		double[][] cB_array = new double[1][getNumOfConstraint()];
		for(int i = 0; i < getNumOfConstraint(); i++) {
			cB_array[0][i] = 0.0;
		}
		setcB(new Matrix(cB_array));
		
		double[][] cN_array = getcN().getArray(); 
		if(getProblemType().equals("min")) {
			for(int i = 0; i < getNumOfVariable(); i++) {
				cN_array[0][i] = -cN_array[0][i];
			}
		}
		
		nonBasicVariables = new String[getNumOfVariable()];
		for (int i = 0; i < getNumOfVariable(); i++) {
			nonBasicVariables[i] = "x" + (i + 1);
		}

		basicVariables = new String[getNumOfConstraint()];
		for (int i = 0; i < getNumOfConstraint(); i++) {
			basicVariables[i] = "x" + (i + getNumOfVariable() + 1);
		}
		
		double[][] I_array = new double[getNumOfConstraint()][getNumOfConstraint()];
		for(int i = 0, j = 0; i < getNumOfConstraint(); i++, j++) {
			I_array[i][j] = 1.0;
		}
		setI(I_array);

		setE(new Matrix(getI()));

		double[][] tmpETA = new double[getNumOfConstraint()][1];
		setEta(new Matrix(tmpETA));
	}

	public void solve() throws HasNoOptimalSolutionException{
		/*
		for(int i = 1; i <= 4; i++) {
			initialize();
			System.out.println("---------- Iteration - " + i + " ----------");
			step1();
			step2();
			if (isOptimal())
				break;
			step3();
			step4();
		}
		*/
		int count = 1;
		if(hasNegative(getBinverse())) {
			double[][] tmpBinverse = getBinverse().getArray();
			Matrix.display(new Matrix(tmpBinverse));
			double[][] tmpB = getB().getArray();
			double[][] tmpN = getN().getArray();
			Matrix.display(new Matrix(tmpBinverse));
			String[] tmp = getConsEq_array();
			for(int i = 0; i < getNumOfConstraint(); i++) {
				if(tmp[i].equals(">=")) {
					System.out.println("Before: " + tmpB[i][0]);
					tmpB[i][0] = (-tmpB[i][0]);
					System.out.println("After: " + tmpB[i][0]);
					for(int j = 0; j < getNumOfConstraint(); j++) {
						tmpBinverse[j][i] = - tmpBinverse[j][i]; 
					}
					for(int j = 0; j < getNumOfVariable(); j++) {
						tmpN[i][j] = - tmpN[i][j]; 
					}
				}
			}
			setN(new Matrix(tmpN));
			setBinverse(new Matrix(tmpBinverse));
			setB(new Matrix(tmpB));
			
		}
		while (!isOptimal()) {

			initialize();

			System.out.println("-------------------- Iteration - " + count + " --------------------");
			step1();
			step2();
			if (isOptimal())
				break;
			try {
				step3();
			} catch(HasNoOptimalSolutionException e) {
				throw e;
			}
			if(!hasOptimal()){
				break;
			}
			step4();
			count++;
		}
	}

	private void initialize() {
		System.out.println("-------------------- Initialization --------------------");
		System.out.println("N: ");
		Matrix.display(getN());

		System.out.println("B inverse: ");
		Matrix.display(getBinverse());

		System.out.println("b: ");
		Matrix.display(getB());

		System.out.println("Basic Variables: ");
		for (int i = 0; i < getBasicVariables().length; i++) {
			System.out.println(basicVariables[i]);
		}

		System.out.println("\ncB: ");
		Matrix.display(getcB());

		System.out.println("Non-Basic Variables: ");
		for (int i = 0; i < getNonBasicVariables().length; i++) {
			System.out.println(nonBasicVariables[i]);
		}

		System.out.println("\ncN: ");
		Matrix.display(getcN());

		System.out.println("xB: ");
		calculate_xB();
		Matrix.display(xB);

		System.out.println("z: ");
		calculate_z();
		Matrix.display(z);
	}

	private void step1() {
		System.out.println("------ Step 1 ------");
		System.out.println("w = cB (x) B_inverse =");
		calculate_w();
		Matrix.display(getW());
	}

	private void step2() {
		System.out.println("------ Step 2 ------");
		System.out.println("zN - cN = w (x) N - cN =");
		calculate_zN_minus_cN();
		Matrix.display(getStep_two());

		if (hasNegative(getStep_two())) {
			findMostNegativeNonBasic();
		} else {
			System.out.println("No Negative Value! Current Basis is OPTIMAL!");
			findOptimal();
		}
	}

	private void step3() throws HasNoOptimalSolutionException{
		System.out.println("------ Step 3 ------");
		int ebv = getEBV_INDEX();
		System.out.println("Update a" + ebv + " as y" + ebv + " = B_inverse (x) a" + ebv + ": ");

		System.out.println("a" + ebv + ":");
		Matrix.display(getEBV_COLUMN());

		System.out.println("y" + ebv + ":");
		Matrix updatedEBV = updateEBV(getEBV_COLUMN());
		Matrix.display(updatedEBV);
		
		System.out.println("Mininum Ratio Test:");
		Matrix minRatio = calculateMinRatio(updatedEBV);
		Matrix.display(minRatio);

		try {
			findLBV(minRatio);
		} catch (HasNoOptimalSolutionException e) {
			throw e;
		}
		
		updateBasis();

		System.out.println("Accoording to min. Ratio test, " + getLBV() + " is the leaving basic variable!");

		System.out.println("\nEta: ");
		calculate_eta();
		Matrix.display(getEta());
	}

	private void step4() {
		System.out.println("------ Step 4 ------");
		System.out.println("New B_inverse = E (x) Old B_inverse:");
		update_E();
		System.out.println("E:");
		Matrix.display(getE());

		update_Binverse();
		System.out.println("New B_inverse:");
		Matrix.display(getBinverse());
	}
	
	//////////////////////////////////////////////////////////////////////
	//////////////////// Revised Simplex Calculations ////////////////////
	//////////////////////////////////////////////////////////////////////
	
	public void calculate_z() {
		try {
			setZ(Matrix.multiply(getcB(), getxB()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void calculate_xB() {
		try {
			setxB(Matrix.multiply(getBinverse(), getB()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calculate_w() {
		try {
			setW(Matrix.multiply(getcB(), getBinverse()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calculate_zN_minus_cN() {
		try {
			setStep_two(Matrix.subtract(Matrix.multiply(getW(), getN()), getcN()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Matrix calculateMinRatio(Matrix updatedEBV) {
		int r1 = getNumOfConstraint();
		double[][] result_arr = new double[r1][1];

		double[][] ebv_arr = updatedEBV.getArray();
		double[][] b_arr = getxB().getArray();

		for (int i = 0; i < r1; i++) {
			if (ebv_arr[i][0] <= 0) {
				// Only Positive Integers enters the Min. Ratio Test
				// For others, their corresponding ratio set to -1
				result_arr[i][0] = -1;
			} else {
				result_arr[i][0] = b_arr[i][0] / ebv_arr[i][0];
				BigDecimal bd = new BigDecimal(result_arr[i][0]).setScale(3, RoundingMode.HALF_UP);
		        double newInput = bd.doubleValue();
		        result_arr[i][0] = newInput;
			}
		}
		Matrix result = new Matrix(result_arr);
		return result;
	}
	
	private void calculate_eta() {
		double[][] ebv_arr = updateEBV(getEBV_COLUMN()).getArray();
		int r1 = getNumOfConstraint();
		int lbvIndex = getLBV_INDEX() - getNumOfVariable() - 1;
		double current = ebv_arr[lbvIndex][0];
		double[][] eta_arr = new double[getNumOfConstraint()][1];
		for (int i = 0; i < r1; i++) {
			if (i == lbvIndex) {
				eta_arr[i][0] = 1 / current;
				BigDecimal bd = new BigDecimal(eta_arr[i][0]).setScale(3, RoundingMode.HALF_UP);
		        double newInput = bd.doubleValue();
		        eta_arr[i][0] = newInput;
			} else {
				eta_arr[i][0] = (0 - ebv_arr[i][0]) / current;
				BigDecimal bd = new BigDecimal(eta_arr[i][0]).setScale(3, RoundingMode.HALF_UP);
		        double newInput = bd.doubleValue();
		        eta_arr[i][0] = newInput;
			}
		}
		setEta(new Matrix(eta_arr));
	}
	
	////////////////////////////////////////////////////////
	//////////////////// Helper Methods ////////////////////
	////////////////////////////////////////////////////////
	
	private void updateBasis() {
		String ebv = getEBV();
		String lbv = getLBV();

		String[] oldNBVariables = getNonBasicVariables();
		String[] oldBVariables = getBasicVariables();

		double[][] oldNB_array = getcN().getArray();
		double[][] newNB_array = new double[1][getNumOfVariable()];

		double[][] oldBV_array = getcB().getArray();
		double[][] newBV_array = new double[1][getNumOfConstraint()];

		for (int i = 0; i < oldNBVariables.length; i++) {
			newNB_array[0][i] = oldNB_array[0][i];
			if (ebv.equals(oldNBVariables[i])) {
				// Replace this variable with lbv
				oldNBVariables[i] = lbv;
				newNB_array[0][i] = oldBV_array[0][getLBV_INDEX() - getNumOfVariable() - 1];
			}
		}

		for (int i = 0; i < oldBVariables.length; i++) {
			newBV_array[0][i] = oldBV_array[0][i];
			if (lbv.equals(oldBVariables[i])) {
				// Replace this variable with ebv
				oldBVariables[i] = ebv;
				newBV_array[0][i] = oldNB_array[0][getEBV_INDEX() - 1];
			}
		}
		setNonBasicVariables(oldNBVariables);
		setcN(new Matrix(newNB_array));

		setBasicVariables(oldBVariables);
		setcB(new Matrix(newBV_array));

		double[][] oldN = getN().getArray();
		for (int i = 0; i < getNumOfVariable(); i++) {
			for (int j = 0; j < getNumOfConstraint(); j++) {
				if (getEBV_INDEX() - 1 == i) {
					oldN[j][i] = I[j][getLBV_INDEX() - getNumOfVariable() - 1];
				}
			}
		}

		setN(new Matrix(oldN));
	}

	private boolean hasNegative(Matrix matrix) {
		for (double[] row : matrix.getArray())
			for (double column : row)
				if (column < 0)
					return true;
		return false;
	}
	
	private boolean hasPositive(Matrix matrix) {
		for (double[] row : matrix.getArray())
			for (double column : row)
				if (column > 0)
					return true;
		return false;
	}

	private void findMostNegativeNonBasic() {
		// Assume this is a n x 1 matrix
		int colNum = getStep_two().getNumberOfColumns();
		double[][] tmp_array = getStep_two().getArray();

		double negative = 0;
		int position = 0;

		for (int i = 0; i < colNum; i++) {
			if (tmp_array[0][i] < negative) {
				negative = tmp_array[0][i];
				position = i + 1;
			}
		}

		setEBV_INDEX(position);
		setEBV("x" + getEBV_INDEX());
		setEBV_COLUMN(getColumnAt(getEBV_INDEX()));

		System.out.println(getEBV() + " has the most negative value.\n" + "Therefore, " + getEBV()
				+ " is the entering basic variable.\n");
	}

	private Matrix updateEBV(Matrix ebv) {
		try {
			return Matrix.multiply(getBinverse(), getEBV_COLUMN());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void findLBV(Matrix minRatio) throws HasNoOptimalSolutionException{
		int r1 = getNumOfConstraint();
		int position = 0;
		double[][] ratio_arr = minRatio.getArray();

		double min = 100000000;
		
		if(hasPositive(minRatio)) {
			for (int i = 0; i < r1; i++) {
				if (ratio_arr[i][0] >= 0)
					if (ratio_arr[i][0] < min) {
						min = ratio_arr[i][0];
						position = i;
					}
			}
			setLBV_INDEX(position + getNumOfVariable() + 1); // 1 must be added, because index starts at 0
			setLBV("x" + getLBV_INDEX());
		}
		else {
			setHasOptimal(false);
			System.out.println("HAS NO OPTIMAL SOLUTION");
			throw new HasNoOptimalSolutionException();
		}
	}

	private void update_E() {
		resetE();
		int c1 = getNumOfVariable();
		int r1 = getNumOfConstraint();
		int column = getLBV_INDEX() - c1 - 1;
		double[][] e_arr = getE().getArray();
		double[][] eta_arr = getEta().getArray();

		for (int i = 0; i < r1; i++) {
			for (int j = 0; j < r1; j++) {
				if (i == column) {
					e_arr[j][i] = eta_arr[j][0];
				}
			}
		}
		Matrix e = new Matrix(e_arr);
		setE(e);
	}

	private void resetE() {
		int r1 = getNumOfConstraint();

		double[][] e_arr = new double[r1][r1];
		for (int i = 0, j = 0; i < r1; i++, j++) {
			e_arr[i][j] = 1;
		}
		Matrix e = new Matrix(e_arr);
		setE(e);
	}

	private void update_Binverse() {
		try {
			setBinverse(Matrix.multiply(getE(), getBinverse()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void findOptimal() {
		System.out.println("---------- OPTIMAL SOLUTION ----------");
		System.out.println("Current basis is optimal!");
		System.out.println("x* =");
		for (int i = 0; i < getBasicVariables().length; i++) {
			System.out.println(basicVariables[i]);
		}
		System.out.println("\nz:");
		Matrix.display(getZ());
		System.out.println("xB:");
		Matrix.display(getxB());

		setOptimal(true);
	}

	private Matrix getColumnAt(int index) {
		int rowNum = getNumOfConstraint();
		double[][] column_array = new double[rowNum][1];
		double[][] N_array = getN().getArray();

		for (int i = 0; i < rowNum; i++) {
			column_array[i][0] = N_array[i][index - 1];
		}

		Matrix column = new Matrix(column_array);
		return column;
	}

	///////////////////////////////////////////////////////////
	//////////////////// GETTERS & SETTERS ////////////////////
	///////////////////////////////////////////////////////////

	public String getObjectiveFunction() {
		return objectiveFunction;
	}

	public void setObjectiveFunction(String objectiveFunction) {
		this.objectiveFunction = objectiveFunction;
	}

	public String getProblemType() {
		return problemType;
	}

	public void setProblemType(String type) {
		this.problemType = type;
	}

	public String getConstrains() {
		return constrains;
	}

	public void setConstrains(String constrains) {
		this.constrains = constrains;
	}

	public Matrix getN() {
		return N;
	}

	public void setN(Matrix n) {
		N = n;
	}

	public Matrix getBinverse() {
		return Binverse;
	}

	public void setBinverse(Matrix binverse) {
		Binverse = binverse;
	}

	public Matrix getB() {
		return b;
	}

	public void setB(Matrix b) {
		this.b = b;
	}

	public String[] getBasicVariables() {
		return basicVariables;
	}

	public void setBasicVariables(String[] basicVariables) {
		this.basicVariables = basicVariables;
	}

	public Matrix getcB() {
		return cB;
	}

	public void setcB(Matrix cB) {
		this.cB = cB;
	}

	public String[] getNonBasicVariables() {
		return nonBasicVariables;
	}

	public void setNonBasicVariables(String[] nonBasicVariables) {
		this.nonBasicVariables = nonBasicVariables;
	}

	public Matrix getcN() {
		return cN;
	}

	public void setcN(Matrix cN) {
		this.cN = cN;
	}

	public Matrix getxB() {
		return xB;
	}

	public void setxB(Matrix xB) {
		this.xB = xB;
	}

	public Matrix getZ() {
		return z;
	}

	public void setZ(Matrix z) {
		this.z = z;
	}

	public Matrix getW() {
		return w;
	}

	public void setW(Matrix w) {
		this.w = w;
	}

	public Matrix getE() {
		return E;
	}

	public void setE(Matrix e) {
		E = e;
	}

	public Matrix getEta() {
		return eta;
	}

	public void setEta(Matrix eta) {
		this.eta = eta;
	}

	public Matrix getStep_two() {
		return step_two;
	}

	public void setStep_two(Matrix step_two) {
		this.step_two = step_two;
	}

	public String getEBV() {
		return EBV;
	}

	public void setEBV(String EBV) {
		this.EBV = EBV;
	}

	public int getEBV_INDEX() {
		return EBV_INDEX;
	}

	public void setEBV_INDEX(int EBV_INDEX) {
		this.EBV_INDEX = EBV_INDEX;
	}

	public String getLBV() {
		return LBV;
	}

	public void setLBV(String LBV) {
		this.LBV = LBV;
	}

	public int getLBV_INDEX() {
		return LBV_INDEX;
	}

	public void setLBV_INDEX(int LBV_INDEX) {
		this.LBV_INDEX = LBV_INDEX;
	}

	public Matrix getEBV_COLUMN() {
		return EBV_COLUMN;
	}

	public void setEBV_COLUMN(Matrix eBV_COLUMN) {
		EBV_COLUMN = eBV_COLUMN;
	}

	public Matrix getLBV_COLUMN() {
		return LBV_COLUMN;
	}

	public void setLBV_COLUMN(Matrix lBV_COLUMN) {
		LBV_COLUMN = lBV_COLUMN;
	}

	public int getNumOfVariable() {
		return numOfVariable;
	}

	public int getNumOfConstraint() {
		return numOfConstraint;
	}

	private void setNumOfVariable(int numOfVariable) {
		this.numOfVariable = numOfVariable;
	}

	private void setNumOfConstraint(int numOfConstraint) {
		this.numOfConstraint = numOfConstraint;
	}

	private boolean isOptimal() {
		return isOptimal;
	}

	private void setOptimal(boolean isOptimal) {
		this.isOptimal = isOptimal;
	}

	private double[][] getI() {
		return I;
	}

	private void setI(double[][] i) {
		I = i;
	}

	private String[] getConsEq_array() {
		return consEq_array;
	}

	public boolean hasOptimal() {
		return hasOptimal;
	}

	private void setHasOptimal(boolean hasOptimal) {
		this.hasOptimal = hasOptimal;
	}
	
	@SuppressWarnings("serial")
	public static class HasNoOptimalSolutionException extends RuntimeException {
		public HasNoOptimalSolutionException() {
			String errorMessage = "This problem has no optimal solution!";
			System.err.println(errorMessage);
		}
	}
}
