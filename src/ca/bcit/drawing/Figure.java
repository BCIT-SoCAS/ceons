package ca.bcit.drawing;

import ca.bcit.utils.geom.Vector2F;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Figure {
	Vector2F startPoint;
	String name;

	Figure(Vector2F startPoint, String name) {
		this.startPoint = startPoint;
		this.name = name;
	}

	Figure() {
		startPoint = new Vector2F(0, 0);
		name = "";
	}

	public abstract void draw(GraphicsContext gc);

	public Vector2F getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Vector2F startPoint) {
		this.startPoint = startPoint;
	}

	protected abstract double calculateDistanceFromPoint(Vector2F p);

	protected abstract void drawOutline(GraphicsContext gc, Color color);

	public abstract int getInfo();

	void drawOutline(GraphicsContext gc) {
		drawOutline(gc, Color.BLACK);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNodeNum(){
		return Integer.parseInt(name.split("_")[1]);
	}

	public boolean equalsByName(String name) {
		return this.name.equals(name);
	}
}
