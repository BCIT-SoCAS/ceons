package mtk.geom;

public class Vector2F extends Matrix2F implements Cloneable {
	
	public Vector2F (float x, float y) {
		super(new float[][] {{x}, {y}});
	}
	
	Vector2F (Matrix2F vector) {
		super(vector.matrix);
	}
	
	@Override
	public Vector2F add(Matrix2F other) {
		return new Vector2F(super.add(other));
	}
	
	@Override
	public Vector2F subtract(Matrix2F other) {
		return new Vector2F(super.subtract(other));
	}
	
	@Override
	public Vector2F negative() {
		return new Vector2F(super.negative());
	}
	
	@Override
	public Vector2F multiply(float scalar) {
		return new Vector2F(super.multiply(scalar));
	}
	
	@Override
	public Vector2F multiply(Matrix2F other) {
		return new Vector2F(super.multiply(other));
	}
	
	public float multiply(Vector2F other) {
		return getX() * other.getX() + getY() * other.getY();
	}
	
	public float getX() {
		return matrix[0][0];
	}
	
	public float getY() {
		return matrix[1 % rows()][1 % columns()];
	}
	
	public float length() {
		return (float) Math.sqrt(multiply(this));
	}
	
	@Override
	public Vector2F clone() {
		return new Vector2F(super.clone());
	}
}
