package calculations;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Matrix {

	private int numberOfRows;
	private int numberOfColumns;

	private double[][] array;

	public Matrix(double[][] arr) {
		this.array = arr;	
		this.numberOfRows = arr.length;
		this.numberOfColumns = arr[0].length;
	}

	public static Matrix add(Matrix first, Matrix second) throws Exception {
		int r1 = first.getNumberOfRows();
		int c1 = first.getNumberOfColumns();
		int r2 = second.getNumberOfRows();
		int c2 = second.getNumberOfColumns();
		
		if (r1 != r2 && c1 != c2) {
			throw new IncorrectDimensionsException("+");
		} else {
			double[][] result_arr = new double[r1][c1];

			for (int i = 0; i < r1; i++) {
				for (int j = 0; j < c2; j++)
					result_arr[i][j] = first.array[i][j] + second.array[i][j];
			}

			Matrix matrix = new Matrix(result_arr);
			return matrix;
		}
	}
	
	public static Matrix subtract(Matrix first, Matrix second) throws Exception {
		int r1 = first.getNumberOfRows();
		int c1 = first.getNumberOfColumns();
		int r2 = second.getNumberOfRows();
		int c2 = second.getNumberOfColumns();
		
		if (r1 != r2 && c1 != c2) {
			throw new IncorrectDimensionsException("-");
		} else {
			double[][] result_arr = new double[r1][c1];

			for (int i = 0; i < r1; i++) {
				for (int j = 0; j < c2; j++) {
					result_arr[i][j] = first.array[i][j] - second.array[i][j];
				}
			}

			Matrix matrix = new Matrix(result_arr);
			return matrix;
		}
	}

	public static Matrix multiply(Matrix first, Matrix second) throws Exception {
		int r1 = first.getNumberOfRows();
		int c1 = first.getNumberOfColumns();
		int r2 = second.getNumberOfRows();
		int c2 = second.getNumberOfColumns();

		if (c1 != r2) {
			throw new IncorrectDimensionsException("*");
		} else {
			double[][] result_arr = new double[r1][c2];
			for (int i = 0; i < r1; i++) {
				for (int j = 0; j < c2; j++) {
					for (int k = 0; k < c1; k++) {
						result_arr[i][j] += first.array[i][k] * second.array[k][j];
						BigDecimal bd = new BigDecimal(result_arr[i][j]).setScale(3, RoundingMode.HALF_UP);
				        double newInput = bd.doubleValue();
				        result_arr[i][j] = newInput;
					}
				}
			}

			Matrix result = new Matrix(result_arr);
			return result;
		}
	}

	public static void display(Matrix matrix) {
		double[][] tmp_array = matrix.getArray();
		for (double[] row : tmp_array) {
			for (double column : row) {
				System.out.print(column + "    ");
			}
			System.out.println();
		}
		System.out.println();
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public int getNumberOfColumns() {
		return numberOfColumns;
	}

	public void setNumberOfColumns(int numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}

	public double[][] getArray() {
		return array;
	}

	public void setArray(double[][] array) {
		this.array = array;
	}

	@SuppressWarnings("serial")
	static class IncorrectDimensionsException extends RuntimeException {
		String errorMessage;
		public IncorrectDimensionsException(String type) {
			if (type.equals("*")) {
				errorMessage = "To multiply two vectors the number of dimensions of first vector must equal number of dimensions of second vector.";
			} else if (type.equals("+") || type.equals("-")) {
				errorMessage = "To add or subsctract matrices, the matrices must have same dimensions.";
			}
			System.err.println(errorMessage);
		}
	}
}
