package mtk.eon.utils.geom;

import java.util.Arrays;

public class FloatMatrix implements Cloneable {
	
	float[][] matrix;
	
	public FloatMatrix(float[][] matrix) {
		this.matrix = matrix;
	}
	
	public FloatMatrix add(FloatMatrix other) {
		if (rows() != other.rows() || columns() != other.columns())
			throw new MatrixException("Cannot add two matrices with different sizes!");
		float[][] matrix = new float[rows()][columns()];
		
		for (int i = 0; i < rows(); i++) for (int j = 0; j < columns(); j++)
			matrix[i][j] = this.matrix[i][j] + other.matrix[i][j];
		
		return new FloatMatrix(matrix);
	}
	
	public FloatMatrix subtract(FloatMatrix other) {
		if (rows() != other.rows() || columns() != other.columns())
			throw new MatrixException("Cannot subtract two matrices with different sizes!");
		float[][] matrix = new float[rows()][columns()];
		
		for (int i = 0; i < rows(); i++) for (int j = 0; j < columns(); j++)
			matrix[i][j] = this.matrix[i][j] - other.matrix[i][j];
		
		return new FloatMatrix(matrix);
	}
	
	public FloatMatrix negative() {
		float[][] matrix = new float[rows()][columns()];
		
		for (int i = 0; i < rows(); i++) for (int j = 0; j < columns(); j++)
			matrix[i][j] = -this.matrix[i][j];
		
		return new FloatMatrix(matrix);
	}
	
	public FloatMatrix multiply(float scalar) {
		float[][] matrix = new float[rows()][columns()];
		
		for (int i = 0; i < rows(); i++) for (int j = 0; j < columns(); j++)
			matrix[i][j] = this.matrix[i][j] * scalar;
		
		return new FloatMatrix(matrix);
	}
	
	public FloatMatrix multiply(FloatMatrix other) {
		if (columns() != other.rows())
			throw new MatrixException("Cannot multiply matrices where number of columns in A is not equal to number of rows in B.");
		float[][] matrix = new float[rows()][other.columns()];
		
		for (int i = 0; i < rows(); i++) for (int j = 0; j < other.columns(); j++)
			for (int k = 0; k < columns(); k++) matrix[i][j] += this.matrix[i][k] * other.matrix[k][j];
		
		return new FloatMatrix(matrix);
	}
	
	public Vector2F multiply(Vector2F other) {
		return new Vector2F(multiply((FloatMatrix) other)); 
	}
	
	public FloatMatrix transpose() {
		float[][] matrix = new float[columns()][rows()];
		
		for (int i = 0; i < rows(); i++) for (int j = 0; j < columns(); j++)
			matrix[i][j] = this.matrix[j][i];
		
		return new FloatMatrix(matrix);
	}
	
	public int rows() {
		return matrix.length;
	}
	
	public int columns() {
		return matrix[0].length;
	}
	
	@Override
	public FloatMatrix clone() {
		float[][] matrix = new float[rows()][columns()];
		
		for (int i = 0; i < rows(); i++) for (int j = 0; j < columns(); j++)
			matrix[i][j] = this.matrix[i][j];
		
		return new FloatMatrix(matrix);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof FloatMatrix) return Arrays.deepEquals(((FloatMatrix) other).matrix, matrix);
		return false;
	}
    public Vector2F getRow(int i)
    {
        return new Vector2F(matrix[i][0],matrix[i][1]);
    }
}
