package ca.bcit.drawing;

import ca.bcit.utils.draw.DashedDrawing;
import ca.bcit.utils.geom.Vector2F;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class Link extends Figure {
	private Vector2F endPoint;
	private int length;
	private int Percentage = 100;

	public Link(Link link) {
		length = link.length;
		name = link.name;
		startPoint = link.startPoint.clone();
		endPoint = link.endPoint.clone();
		loadImage();
	}

	/**
	 * @deprecatd Old method to draw link with the previous used color (default green)
	 * @param stPoint starting location of the link
	 * @param _endPoint end location of the link
	 * @param number the number tag identifying the link
	 */
	@Deprecated
	public Link(Vector2F stPoint, Vector2F _endPoint, int number) {
		super(stPoint, "Link" + number);
		endPoint = _endPoint;
		length = 0;
		loadImage();
	}

	/**
	 * Draw link with a gradient color acording to avaliable free slices
	 * @param stPoint starting location of the link
	 * @param _endPoint end location of the link
	 * @param number the number tag identifying the link
	 * @param Percentage percentage of free slices left in the link
	 */
	public Link(Vector2F stPoint, Vector2F _endPoint, int number, int Percentage) {
		super(stPoint, "Link" + number);
		this.Percentage = Percentage;
		endPoint = _endPoint;
		length = 0;
		loadImage();
	}

	/**
	 * @deprecated Old method to draw link with the previous used color (default green)
	 * @param _startPoint starting location of the link
	 * @param _endPoint end location of the link
	 * @param _name the name identifying the link
	 */
	@Deprecated
	public Link(Vector2F _startPoint, Vector2F _endPoint, String _name) {
		super(_startPoint, _name);
		endPoint = _endPoint;
		length = 0;
		loadImage();
	}

	/**
	 * @deprecated Old method to draw link with the previous used color of specified length(default green)
	 * @param _startPoint starting location of the link
	 * @param _endPoint end location of the link
	 * @param _name the name identifying the link
	 * @param _length the length of the link to b drawn
	 */
	@Deprecated
	public Link(Vector2F _startPoint, Vector2F _endPoint, String _name, int _length) {
		super(_startPoint, _name);
		endPoint = _endPoint;
		length = _length;
		loadImage();
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

		gc.setLineWidth(Node.imageSize / 2);
		gc.setStroke(Color.web("rgb(" + rgb[0] + ',' + rgb[1] + ',' + rgb[2] + ')'));
		gc.strokeLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
		gc.strokeLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
		gc.setLineWidth(Node.imageSize / 2 - Node.imageSize / 4);
		gc.setStroke(Color.WHITE);
		gc.strokeLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
		float fill = 1f;
		gc.setLineWidth((Node.imageSize / 2 - Node.imageSize / 4) * fill);
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
		} else {
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
	protected void loadImage() {

	}

	@Override
	protected void drawOutline(GraphicsContext gc, Color color) {
		int dx = 0, dy = (int) (Node.imageSize/4);
		if (Math.abs(endPoint.getX() - startPoint.getX()) < 50) {
			dx = (int)Node.imageSize/4;
			dy = 0;
		}
		Vector2F newStartPoint = new Vector2F(startPoint.getX() + dx,
				startPoint.getY() + dy);
		Vector2F newEndPoint = new Vector2F(endPoint.getX() + dx,
				endPoint.getY() + dy);
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
			float x1 = startPoint.getX() - Node.imageSize / 2;
			float y1 = startPoint.getY() - Node.imageSize / 2;
			float x2 = endPoint.getX() - Node.imageSize / 2;
			float y2 = endPoint.getY() - Node.imageSize / 2;
			float a = (-y2 + y1) / (x2 - x1);
			float b = -y1 - ((-y2 + y1) / (x2 - x1)) * x1;
			double odleglosc = (Math.abs(a * p.getX() + p.getY() + b)) / Math.sqrt(1 + a * a);
			return odleglosc + Node.imageSize / 2;
		}
		else {
			float dist1 = startPoint.distance(p);
			float dist2 = endPoint.distance(p);
			if (dist1 > dist2)
				return dist2 + Node.imageSize;
			else
				return dist1 + Node.imageSize;
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

	public void setLength(int _length) {
		length = _length;
	}
}
