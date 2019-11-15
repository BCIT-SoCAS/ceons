package ca.bcit.drawing;

import ca.bcit.Settings;
import ca.bcit.utils.draw.DashedDrawing;
import ca.bcit.utils.geom.Vector2F;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Link extends Figure {
	private Vector2F endPoint;
	private int length;
	private int Percentage = 100;

	public Link(Link link) {
		length = link.length;
		name = link.name;
		startPoint = link.startPoint.clone();
		endPoint = link.endPoint.clone();
	}

	/**
	 * Draw link with a gradient color acording to avaliable free slices
	 * @param stPoint starting location of the link
	 * @param endPoint end location of the link
	 * @param number the number tag identifying the link
	 * @param Percentage percentage of free slices left in the link
	 * @param length length between start node and end node
	 */
	public Link(Vector2F stPoint, Vector2F endPoint, int number, int Percentage, int length) {
		super(stPoint, "Link" + number);
		this.Percentage = Percentage;
		this.endPoint = endPoint;
		this.length = length;
	}

	/**
	 * @deprecatd Old method to draw link with the previous used color (default green)
	 * @param stPoint starting location of the link
	 * @param endPoint end location of the link
	 * @param number the number tag identifying the link
	 */
	@Deprecated
	public Link(Vector2F stPoint, Vector2F endPoint, int number) {
		super(stPoint, "Link" + number);
		this.endPoint = endPoint;
		length = 0;
	}

	/**
	 * @deprecated Old method to draw link with the previous used color (default green)
	 * @param startPoint starting location of the link
	 * @param endPoint end location of the link
	 * @param name the name identifying the link
	 */
	@Deprecated
	public Link(Vector2F startPoint, Vector2F endPoint, String name) {
		super(startPoint, name);
		this.endPoint = endPoint;
		length = 0;
	}

	/**
	 * @deprecated Old method to draw link with the previous used color of specified length(default green)
	 * @param startPoint starting location of the link
	 * @param endPoint end location of the link
	 * @param name the name identifying the link
	 * @param length the length of the link to b drawn
	 */
	@Deprecated
	public Link(Vector2F startPoint, Vector2F endPoint, String name, int length) {
		super(startPoint, name);
		this.endPoint = endPoint;
		this.length = length;
	}

	@Override
	public int getInfo() {
		return this.Percentage;
	}

	/**
	 * Method to draw the actual links. Color parameters are passed in from other methods
	 * @param gc graphic context objct with the draw functions
	 */
	@Override
	public void draw(GraphicsContext gc) {
		int[] rgb = getColor();

		float lineWidth = Node.getNodeSize() / 2;
		float fillWidth = lineWidth / 2;
		gc.setLineWidth(lineWidth);
		gc.setStroke(Color.web("rgb(" + rgb[0] + ',' + rgb[1] + ',' + rgb[2] + ')'));
		float startX = startPoint.getX() - Settings.topLeftCornerXCoordinate;
		float startY = startPoint.getY() - Settings.topLeftCornerYCoordinate;
		float endX = endPoint.getX() - Settings.topLeftCornerXCoordinate;
		float endY = endPoint.getY() - Settings.topLeftCornerYCoordinate;
		gc.strokeLine(startX, startY, endX, endY);
		gc.strokeLine(startX, startY, endX, endY);
		gc.setLineWidth(fillWidth);
		gc.setStroke(Color.WHITE);
		gc.strokeLine(startX, startY, endX, endY);
		float fill = 1f;
		gc.setLineWidth((fillWidth) * fill);
	}

	/**
	 * Takes the link's free slice percentage, and convert it to an rgb gradient.
	 * @return an array with rgb color
	 */
	private int[] getColor() {
		int[] rgb = new int[3];
		if (this.Percentage > 50) {
			rgb[0] = (50 - (this.Percentage - 50)) * 5;
			rgb[1] = 255;
		}
		else {
			rgb[0] = 255;
			rgb[1] = this.Percentage * 5;
		}
		return rgb;
	}

	@Override
	public boolean equals(Object o) {
		Link vex = (Link) o;
		return (startPoint.equals(vex.startPoint) && endPoint.equals(vex.endPoint) || (startPoint.equals(vex.endPoint) && endPoint.equals(vex.startPoint)));
	}

	@Override
	protected void drawOutline(GraphicsContext gc, Color color) {
		int dx = 0, dy = (int) (Node.getNodeSize()/4);
		if (Math.abs(endPoint.getX() - startPoint.getX()) < 50) {
			dx = (int)Node.getNodeSize()/4;
			dy = 0;
		}
		Vector2F newStartPoint = new Vector2F(startPoint.getX() + dx, startPoint.getY() + dy);
		Vector2F newEndPoint = new Vector2F(endPoint.getX() + dx, endPoint.getY() + dy);
		DashedDrawing.drawDashedLine(gc, newStartPoint, newEndPoint, color);
		newStartPoint = new Vector2F(startPoint.getX() - dx, startPoint.getY() - dy);
		newEndPoint = new Vector2F(endPoint.getX() - dx, endPoint.getY() - dy);
		DashedDrawing.drawDashedLine(gc, newStartPoint, newEndPoint, color);
		newStartPoint = new Vector2F(startPoint.getX() - dx, startPoint.getY() - dy);
		newEndPoint = new Vector2F(startPoint.getX() + dx, startPoint.getY() + dy);
		DashedDrawing.drawDashedLine(gc, newStartPoint, newEndPoint, color);
		newEndPoint = new Vector2F(endPoint.getX() + dx, endPoint.getY() + dy);
		newStartPoint = new Vector2F(endPoint.getX() - dx, endPoint.getY() - dy);
		DashedDrawing.drawDashedLine(gc, newStartPoint, newEndPoint, color);
	}

	@Override
	protected double calculateDistanceFromPoint(Vector2F p) {
		if ((p.getX() + 5 > startPoint.getX() && p.getX() - 5 < endPoint.getX()) || (p.getX() + 5 > endPoint.getX() && p.getX() - 5 < startPoint.getX())) {
			float x1 = startPoint.getX() - Node.getNodeSize() / 2;
			float y1 = startPoint.getY() - Node.getNodeSize() / 2;
			float x2 = endPoint.getX() - Node.getNodeSize() / 2;
			float y2 = endPoint.getY() - Node.getNodeSize() / 2;
			float a = (-y2 + y1) / (x2 - x1);
			float b = -y1 - ((-y2 + y1) / (x2 - x1)) * x1;
			double odleglosc = (Math.abs(a * p.getX() + p.getY() + b)) / Math.sqrt(1 + a * a);
			return odleglosc + Node.getNodeSize() / 2;
		}
		else {
			float dist1 = startPoint.distance(p);
			float dist2 = endPoint.distance(p);
			if (dist1 > dist2)
				return dist2 + Node.getNodeSize();
			else
				return dist1 + Node.getNodeSize();
		}
	}

	@Override
	public void drawOutline(GraphicsContext gc) {
		drawOutline(gc, Color.GRAY);
	}

	public void setEndPoint(Vector2F p) {
		endPoint = p;
	}

	public Vector2F getEndPoint() {
		return endPoint;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
}
