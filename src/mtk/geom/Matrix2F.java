package mtk.geom;

public class Matrix2F implements Cloneable {
	
	float[][] matrix;
	
	public Matrix2F(float[][] matrix) {
		this.matrix = matrix;
	}
	
	public Matrix2F add(Matrix2F other) {
		if (rows() != other.rows() || columns() != other.columns())
			throw new MatrixException("Cannot add two matrices with different sizes!");
		float[][] matrix = new float[rows()][columns()];
		
		for (int i = 0; i < rows(); i++) for (int j = 0; j < columns(); j++)
			matrix[i][j] = this.matrix[i][j] + other.matrix[i][j];
		
		return new Matrix2F(matrix);
	}
	
	public Matrix2F subtract(Matrix2F other) {
		if (rows() != other.rows() || columns() != other.columns())
			throw new MatrixException("Cannot subtract two matrices with different sizes!");
		float[][] matrix = new float[rows()][columns()];
		
		for (int i = 0; i < rows(); i++) for (int j = 0; j < columns(); j++)
			matrix[i][j] = this.matrix[i][j] - other.matrix[i][j];
		
		return new Matrix2F(matrix);
	}
	
	public Matrix2F negative() {
		float[][] matrix = new float[rows()][columns()];
		
		for (int i = 0; i < rows(); i++) for (int j = 0; j < columns(); j++)
			matrix[i][j] = -this.matrix[i][j];
		
		return new Matrix2F(matrix);
	}
	
	public Matrix2F multiply(float scalar) {
		float[][] matrix = new float[rows()][columns()];
		
		for (int i = 0; i < rows(); i++) for (int j = 0; j < columns(); j++)
			matrix[i][j] = this.matrix[i][j] * scalar;
		
		return new Matrix2F(matrix);
	}
	
	public Matrix2F multiply(Matrix2F other) {
		if (columns() != other.rows())
			throw new MatrixException("Cannot multiply matrices where number of columns in A is not equal to number of rows in B.");
		float[][] matrix = new float[rows()][other.columns()];
		
		for (int i = 0; i < rows(); i++) for (int j = 0; j < other.columns(); j++)
			for (int k = 0; k < columns(); k++) matrix[i][j] += this.matrix[i][k] * other.matrix[k][j];
		
		return new Matrix2F(matrix);
	}
	
	public Vector2F multiply(Vector2F other) {
		return new Vector2F(multiply((Matrix2F) other)); 
	}
	
	public Matrix2F transpose() {
		float[][] matrix = new float[columns()][rows()];
		
		for (int i = 0; i < rows(); i++) for (int j = 0; j < columns(); j++)
			matrix[i][j] = this.matrix[j][i];
		
		return new Matrix2F(matrix);
	}
	
	public int rows() {
		return matrix.length;
	}
	
	public int columns() {
		return matrix[0].length;
	}
	
	@Override
	public Matrix2F clone() {
		float[][] matrix = new float[rows()][columns()];
		
		for (int i = 0; i < rows(); i++) for (int j = 0; j < columns(); j++)
			matrix[i][j] = this.matrix[i][j];
		
		return new Matrix2F(matrix);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Matrix2F) return ((Matrix2F) other).matrix.equals(matrix);
		return false;
	}
    public Vector2F getRow(int i)
    {
        return new Vector2F(matrix[i][0],matrix[i][1]);
    }
}