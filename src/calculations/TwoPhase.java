package calculations;

import java.math.BigDecimal;
import java.math.RoundingMode;

import calculations.SolveRS.HasNoOptimalSolutionException;

public class TwoPhase {
	private String[] consEq_array;
	private int numOfVariable;
	private int numOfConstraint;
	private int numOfSlack;
	private int numOfArtificial;

	private double[][] new_N_array;
	private double[][] obj_row;
	double[][] cN_array;
	double[][] N_array;
	double[][] b_array;
	double[][] Binverse_array;

	double[][] z;

	private boolean isOptimal = true;
	private boolean isFeasible = true;
	
	private int EBV_INDEX;

	private int LBV_INDEX;
	
	private int numOfTotalRow;
	private int numOfTotalColumn;

	public TwoPhase(int numOfVariable, int numOfConstraint, double[][] cN_array, double[][] N_array, double[][] b_array, double[][] Binverse_array,
			String[] consEq_array) {
		this.numOfVariable = numOfVariable;
		this.numOfConstraint = numOfConstraint;
		this.cN_array = cN_array;
		this.N_array = N_array;
		this.b_array = b_array;
		this.Binverse_array = Binverse_array;
		this.consEq_array = consEq_array;
		
		System.out.println("//////////////////// 2 PHASE - START ////////////////////");
		initialize();
		solve();
	}
	
	private void initialize() {
		z = new double[1][1];
		z[0][0] = 0;
		int tmpNumPositiveSlack = 0;
		int tmpNumNegativeSlack = 0;
		int tmpNumArtificial = 0;
		for (String str : consEq_array) {
			if (str.equals("<=")) {
				tmpNumPositiveSlack++;
			} else if (str.equals("=")) {
				tmpNumArtificial++;
			} else if (str.equals(">=")) {
				tmpNumNegativeSlack++;
				tmpNumArtificial++;
			}
		}
		setNumOfSlack(tmpNumPositiveSlack + tmpNumNegativeSlack);
		setNumOfArtificial(tmpNumArtificial);
		
		setNumOfTotalColumn(getNumOfArtificial() + tmpNumPositiveSlack);
		setNumOfTotalRow(getNumOfVariable() + getNumOfSlack() + getNumOfArtificial());
		
		new_N_array = new double[getNumOfTotalColumn()][getNumOfTotalRow()];
		double[][] tmpNew_N = new double[getNumOfTotalColumn()][getNumOfTotalRow()];
		double[][] tmpN = getN_array();
		
		obj_row = new double[1][getNumOfTotalRow()];
		double[][] tmpObj_row = new double[1][getNumOfTotalRow()];
		
		for (int i = 0; i < getNumOfVariable(); i++) {
			tmpObj_row[0][i] = 0;
			for (int j = 0; j < getNumOfTotalColumn(); j++) {
				tmpNew_N[j][i] = tmpN[j][i];
			}
		}

		for (int i = getNumOfVariable(); i < getNumOfVariable() + getNumOfSlack(); i++) {
			tmpObj_row[0][i] = 0;	
			for (int j = 0; j < getConsEq_array().length; j++) {
				if (consEq_array[j].equals(">=")) {
						tmpNew_N[j][getNumOfVariable()+getNumOfSlack()+j-2] = -1;
				} else if (consEq_array[j].equals("<=")) {
						tmpNew_N[j][getNumOfVariable()+getNumOfSlack()+j-2] = 1;
				} 
			}
		}
		
		for (int i = getNumOfVariable() + getNumOfSlack(), j = 0; i < getNumOfTotalRow(); i++, j++) {
			tmpNew_N[j][i] = 1;
			tmpObj_row[0][i] = -1;
		}
		
		double[][] tmpBinverse = new double[getNumOfTotalColumn()][getNumOfTotalColumn()];
		for(int i = 0, j = 0; i < getNumOfTotalColumn(); i++, j++) {
			tmpBinverse[i][j] = 1;
		}
		setBinverse_array(tmpBinverse);
		setObj_Row(tmpObj_row);
		setNew_N_array(tmpNew_N);
		
		display();
	}
	
	private void display(){
		System.out.println("-----------------------------------------");
		Matrix.display(new Matrix(getObj_Row()));
		Matrix.display(new Matrix(getNew_N_array()));
		System.out.println("RHS");
		Matrix.display(new Matrix(z));
		Matrix.display(new Matrix(getB_array()));
	}
	
	private void solve() {
		for (int j = 0; j < numOfArtificial; j++) {
			z[0][0] += b_array[j][0];
			for (int i = 0; i < numOfVariable + numOfSlack + numOfArtificial; i++) {
				obj_row[0][i] += new_N_array[j][i];
			}
		}
		/*
		for(int i = 1; i <= 4; i++) {
			System.out.println("-------------------- Iteration - " + i + " --------------------");
			
			findMostPositiveNonBasic();
			
			System.out.println("Min Ratio");
			Matrix minRatio = calculateMinRatio(getColumnAt(getEBV_INDEX())); 
			Matrix.display(minRatio);
			
			findLBV(minRatio);
			System.out.println("According to min ratio test Row " + getLBV_INDEX() + " is leaving");
			
			change(getEBV_INDEX()-1, getLBV_INDEX()-1);

			display();
			checkOptimality();
			checkFeasilibility();
			System.out.println(isOptimal());
			if(isFinished()) {
				break;
			}
		}
		*/
		int count = 1;
		while (true) {
			System.out.println("-------------------- Iteration - " + count + " --------------------");
			
			findMostPositiveNonBasic();
			
			System.out.println("Min Ratio");
			Matrix minRatio = calculateMinRatio(getColumnAt(getEBV_INDEX())); 
			Matrix.display(minRatio);
			
			findLBV(minRatio);
			System.out.println("According to min ratio test Row " + getLBV_INDEX() + " is leaving");
			
			change(getEBV_INDEX()-1, getLBV_INDEX()-1);

			display();
			checkOptimality();
			checkFeasilibility();
			count++;
			if(isFinished()) {
				break;
			}
		}
		System.out.println("//////////////////// 2 PHASE - FINISH ////////////////////");
		System.out.println("--------------------------------------");
		System.out.println("New values for Revised Simplex Method:");
		
		
		updateN();
		System.out.println("New N: ");
		Matrix.display(new Matrix(getN_array()));
		
		updateBinverse();
		System.out.println("New Binverse: ");
		Matrix.display(new Matrix(getBinverse_array()));
		
		System.out.println("New b: ");
		Matrix.display(new Matrix(getB_array()));
		System.out.println("--------------------------------------");
		System.out.println("Revised Simplex Continues...\n");
	}
	
	private void findMostPositiveNonBasic() {
		// Assume this is a n x 1 matrix
		int colNum = obj_row[0].length;
		double[][] tmp_array = obj_row;

		double positive = 0;
		int position = 0;

		for (int i = 0; i < colNum; i++) {
			if (tmp_array[0][i] > positive) {
				positive = tmp_array[0][i];
				position = i + 1;
			}
		}
		setEBV_INDEX(position);
		
		System.out.println("Most positive one is " + positive + " at Column " + getEBV_INDEX());
		
		for (int i = 0; i < colNum - numOfArtificial; i++) {
			if (tmp_array[0][i] > positive) {
				positive = tmp_array[0][i];
				position = i + 1;
			}
		}
	}
	
	private void change(int ebv, int lbv) {
		double value = 0;
		double[][] originalN = new double[getNumOfTotalColumn()][getNumOfTotalRow()];
		for (int i = 0; i < getNumOfTotalRow(); i++) {
			for (int j = 0; j < getNumOfTotalColumn(); j++) {
				originalN[j][i] = new_N_array[j][i];
			}
		}
		
		double[][] updatedN = getNew_N_array();
		
		double[][] updatedOF = getObj_Row();
		
		double[][] tmp = new double[1][getNumOfTotalRow()];
		
		// Find the value of the intersection of lbv and ebv
		for(int i = 0; i < getNumOfTotalRow(); i++) {
			for(int j = 0; j < getNumOfTotalColumn(); j++) {
				value = new_N_array[lbv][ebv];
			}
		}
		
		// Divide lbv row and corresponding lbv b by this value 
		for(int i = 0; i < getNumOfTotalRow(); i++) {
			BigDecimal bd = new BigDecimal(new_N_array[lbv][i] / value).setScale(3, RoundingMode.HALF_UP);
			new_N_array[lbv][i] = bd.doubleValue();
			tmp[0][i] = new_N_array[lbv][i];
		}
		
		BigDecimal bd1 = new BigDecimal(b_array[lbv][0] / value).setScale(2, RoundingMode.HALF_UP);
		b_array[lbv][0] = bd1.doubleValue();
		
		double tmpZ = obj_row[0][ebv];
		
		for(int i = 0; i < getNumOfTotalRow(); i++) {
    		if(tmpZ <= 0) {
    			updatedOF[0][i] += tmp[0][i] * tmpZ;
    		} else if(tmpZ > 0) {
    			updatedOF[0][i] -= tmp[0][i] * tmpZ;
    		}
    		
			BigDecimal bd = new BigDecimal(updatedOF[0][i]).setScale(2, RoundingMode.HALF_UP);
	        double newInput = bd.doubleValue();
	        updatedOF[0][i] = newInput;
	        
	        for(int j = 0; j < getNumOfTotalColumn(); j++) {
	        	
	        	if(j != lbv) {
	        		final double tmpN = originalN[j][ebv];
	        		if(tmpN <= 0) {
	        			updatedN[j][i] += tmp[0][i] * (-tmpN);
	        		} else if(tmpN * value > 0) {
	        			updatedN[j][i] -= tmp[0][i] * tmpN;
	        		}
	        		
	    			BigDecimal bd2 = new BigDecimal(updatedN[0][i]).setScale(2, RoundingMode.HALF_UP);
	    	        double newInput2 = bd2.doubleValue();
	    	        updatedN[0][i] = newInput2;
	        	}
	        }
		}
		
        for(int j = 0; j < getNumOfTotalColumn(); j++) {
        	if(j != lbv) {
        		final double tmpN = originalN[j][ebv];
        		if(tmpN <= 0) {
        			b_array[j][0] += b_array[lbv][0] * (-tmpN);
        		} else if(tmpN > 0) {
        			b_array[j][0] -= b_array[lbv][0] * tmpN;
        		}
        		BigDecimal bd2 = new BigDecimal(b_array[j][0]).setScale(2, RoundingMode.HALF_UP);
        		b_array[j][0] = bd2.doubleValue();
        	}
        }
		
		if(tmpZ <= 0) {
			z[0][0] -= b_array[lbv][0] * tmpZ;
		} else if(tmpZ > 0) {
			z[0][0] -= b_array[lbv][0] * tmpZ;
    		BigDecimal bd = new BigDecimal(z[0][0]).setScale(2, RoundingMode.HALF_UP);
    		z[0][0] = bd.doubleValue();
		}
		
		obj_row = updatedOF;
		new_N_array = updatedN;
	}
	
	private void updateN() {
		for (int i = 0; i < getNumOfVariable(); i++) {
			obj_row[0][i] = 0;
			for (int j = 0; j < getNumOfTotalColumn(); j++) {
				N_array[j][i] = new_N_array[j][i];
			}
		}
	}
	
	private void updateBinverse() {
		double[][] tmpBinverse = getBinverse_array();
		for (int i = getNumOfVariable() + getNumOfSlack(); i < getNumOfTotalRow(); i++) {
			obj_row[0][i] = 0;
			for (int j = 0; j < getNumOfTotalColumn(); j++) {
				tmpBinverse[i - getNumOfVariable() - getNumOfSlack()][j] = new_N_array[j][i];
			}
		}
		setBinverse_array(tmpBinverse);
	}
	
	private Matrix calculateMinRatio(Matrix EBV) {
		int r1 = numOfConstraint;
		double[][] result_arr = new double[r1][1];

		double[][] ebv_arr = EBV.getArray();
		double[][] b_arr = b_array;

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
	
	private void findLBV(Matrix minRatio) throws HasNoOptimalSolutionException{
		int r1 = numOfConstraint;
		int position = 0;
		double[][] ratio_arr = minRatio.getArray();

		double min = 100000000;
		
			for (int i = 0; i < r1; i++) {
				if (ratio_arr[i][0] >= 0)
					if (ratio_arr[i][0] < min) {
						min = ratio_arr[i][0];
						position = i;
					}
			}
			setLBV_INDEX(position + 1); // 1 must be added, because index starts at 0
	}
	
	private Matrix getColumnAt(int index) {
		int rowNum = numOfConstraint;
		double[][] column_array = new double[rowNum][1];
		double[][] N_arr = new_N_array;

		for (int i = 0; i < rowNum; i++) {
			column_array[i][0] = N_arr[i][index - 1];
		}

		Matrix column = new Matrix(column_array);
		return column;
	}
	
	private boolean hasPositive(Matrix matrix) {
		for (double[] row : matrix.getArray())
			for (double column : row)
				if (column > 0)
					return true;
		return false;
	}
	
	private void checkOptimality() {
		if(hasPositive(new Matrix(obj_row))) {
			setOptimal(false);
		} else {
			setOptimal(true);
		}
	}
	
	private void checkFeasilibility() {
		for(int i = 0; i < numOfConstraint; i++) {
			if(b_array[i][0] < 0) {
				setFeasible(false);
			}
		}
		setFeasible(true);
	}
	
	private boolean isFinished() {
		if(isOptimal() && isFeasible()) {
			return true; // Yes, is finished
		}
		return false;
	}
	
	public int getLBV_INDEX() {
		return LBV_INDEX;
	}

	public void setLBV_INDEX(int LBV_INDEX) {
		this.LBV_INDEX = LBV_INDEX;
	}

	private int getEBV_INDEX() {
		return EBV_INDEX;
	}

	private void setEBV_INDEX(int eBV_INDEX) {
		EBV_INDEX = eBV_INDEX;
	}

	private boolean isOptimal() {
		return isOptimal;
	}

	private void setOptimal(boolean isOptimal) {
		this.isOptimal = isOptimal;
	}
	
	private boolean isFeasible() {
		return isFeasible;
	}

	private void setFeasible(boolean isFeasible) {
		this.isFeasible = isFeasible;
	}

	private String[] getConsEq_array() {
		return consEq_array;
	}

	private int getNumOfVariable() {
		return numOfVariable;
	}
	
	private int getNumOfSlack() {
		return numOfSlack;
	}

	private void setNumOfSlack(int numOfSlack) {
		this.numOfSlack = numOfSlack;
	}

	private int getNumOfArtificial() {
		return numOfArtificial;
	}

	private void setNumOfArtificial(int numOfArtificial) {
		this.numOfArtificial = numOfArtificial;
	}

	private double[][] getNew_N_array() {
		return new_N_array;
	}

	private void setNew_N_array(double[][] new_N_array) {
		this.new_N_array = new_N_array;
	}

	private double[][] getObj_Row() {
		return obj_row;
	}

	private void setObj_Row(double[][] obj_row) {
		this.obj_row = obj_row;
	}

	public double[][] getcN_array() {
		return cN_array;
	}

	public double[][] getN_array() {
		return N_array;
	}

	public double[][] getB_array() {
		return b_array;
	}

	public double[][] getBinverse_array() {
		return Binverse_array;
	}

	public int getNumOfTotalRow() {
		return numOfTotalRow;
	}

	public void setNumOfTotalRow(int numOfTotalRow) {
		this.numOfTotalRow = numOfTotalRow;
	}

	public int getNumOfTotalColumn() {
		return numOfTotalColumn;
	}

	public void setNumOfTotalColumn(int numOfTotalColumn) {
		this.numOfTotalColumn = numOfTotalColumn;
	}

	private void setBinverse_array(double[][] binverse_array) {
		Binverse_array = binverse_array;
	}
	
	/*
	 * TO CHECK 2 PHASE
	public static void main(String[] args) {
		double[][] N_Array = { {3, 2, -3, 4}, {1, 1, -5, -6}, {2, -2, 1, 1}};
		
		double[][] Binverse_array = { {1, 0, 0}, {0,1,0}, {0,0,1}};

		double[][] b_Array = {{12}, {10}, {10}};

		double[][] cN_Array = { {6, 6, 2, 4} };

		String[] consEqualities = { "=", "<=", "<="};

		TwoPhase a = new TwoPhase(4, 3, cN_Array, N_Array, b_Array, Binverse_array, consEqualities);
	}
	*/
}
