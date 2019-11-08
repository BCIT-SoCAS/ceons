package ca.bcit.utils.geom;

public class Vector2F extends FloatMatrix implements Cloneable {
	public Vector2F (float x, float y) {
		super(new float[][] {{x}, {y}});
	}

	Vector2F (FloatMatrix vector) {
		super(vector.matrix);
	}

	public float distance(Vector2F other) {
		return subtract(other).length();
	}
	
	public Vector2F unit() {
		return multiply(1f / length());
	}
	
	@Override
	public Vector2F add(FloatMatrix other) {
		return new Vector2F(super.add(other));
	}
	
	@Override
	public Vector2F subtract(FloatMatrix other) {
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
	
	private float dot(Vector2F other) {
		return getX() * other.getX() + getY() * other.getY();
	}
	
	public float getX() {
		return matrix[0][0];
	}
	
	public float getY() {
		return matrix[1 % rows()][1 % columns()];
	}
	
	private float length() {
		return (float) Math.sqrt(dot(this));
	}
	
	@Override
	public Vector2F clone() {
		return new Vector2F(super.clone());
	}
}
