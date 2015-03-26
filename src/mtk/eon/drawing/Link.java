package mtk.eon.drawing;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import mtk.eon.utils.draw.DashedDrawing;
import mtk.eon.utils.geom.Vector2F;

public class Link extends Figure {
	protected Vector2F endPoint;
	private int length;
	float fill = 0; //(float) Math.random();

	public Link(Link link) {
		length = link.length;
		name = link.name;
		startPoint = link.startPoint.clone();
		endPoint = link.endPoint.clone();
		loadImage();
	}

	public Link(Vector2F stPoint, Vector2F _endPoint, int number) {
		super(stPoint, "Link" + number);
		endPoint = _endPoint;
		length = 0;
		loadImage();
	}

	public Link(Vector2F _startPoint, Vector2F _endPoint, String _name) {
		super(_startPoint, _name);
		endPoint = _endPoint;
		length = 0;
		loadImage();
	}

	public Link(Vector2F _startPoint, Vector2F _endPoint, String _name, int _length) {
		super(_startPoint, _name);
		endPoint = _endPoint;
		length = _length;
		loadImage();
	}

	@Override
	public void draw(GraphicsContext gc) {
		gc.setLineWidth(Node.imageSize / 2);
		gc.setStroke(Color.PINK);
		gc.strokeLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
		gc.setLineWidth(Node.imageSize / 2 - Node.imageSize / 12);
		Vector2F temp = startPoint.subtract(endPoint).unit().multiply(Node.imageSize / 4f - Node.imageSize / 24f);
		gc.setStroke(new LinearGradient(startPoint.getX() - temp.getY(), startPoint.getY() + temp.getX(), startPoint.getX() + temp.getY(), startPoint.getY() -temp.getX(), false, CycleMethod.NO_CYCLE,
				new Stop[] {new Stop(0, Color.MAGENTA), new Stop(0.15f, Color.PURPLE), new Stop(0.85f, Color.PURPLE), new Stop(1f, Color.MAGENTA)}));
		gc.strokeLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
		gc.setLineWidth(Node.imageSize / 2 - Node.imageSize / 4);
		gc.setStroke(Color.WHITE);
		gc.strokeLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
		gc.setLineWidth((Node.imageSize / 2 - Node.imageSize / 4) * fill);
		gc.setStroke(Color.hsb(120.0 + fill * 180, 0.5 + 0.5 * fill, 1  - 0.5 * fill));
		gc.strokeLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
	}

	@Override
	public boolean equals(Object o) {
		Link vex = (Link) o;
		return (startPoint.equals(vex.startPoint) && endPoint.equals(vex.endPoint) || (startPoint.equals(vex.endPoint) && endPoint.equals(vex.startPoint)));
	}

	@Override
	protected void loadImage() {
		//image = new Image(getClass().getResourceAsStream("line_image.jpg"));
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
		// jezeli punkt miesci sie w przedziale x nalezacych do prostej to
		// obliczam jego dlugosc od prostej
		if ((p.getX() + 5 > startPoint.getX() && p.getX() - 5 < endPoint.getX()) || (p.getX() + 5 > endPoint.getX() && p.getX() - 5 < startPoint.getX())) {
			float x1 = startPoint.getX() - Node.imageSize / 2;
			float y1 = startPoint.getY() - Node.imageSize / 2;
			float x2 = endPoint.getX() - Node.imageSize / 2;
			float y2 = endPoint.getY() - Node.imageSize / 2;
			// wyznaczenie rownania prostej
			float a = (-y2 + y1) / (x2 - x1);
			float b = -y1 - ((-y2 + y1) / (x2 - x1)) * x1;
			// wyyznaczenie odleglosci punktu od prostej
			double odleglosc = (Math.abs(a * p.getX() + p.getY() + b)) / Math.sqrt(1 + a * a);
			return odleglosc + Node.imageSize / 2;
		}
		// jesli to nie to obliczam jego odleglosc od punktow koncowych
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
