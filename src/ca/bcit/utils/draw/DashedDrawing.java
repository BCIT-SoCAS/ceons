package ca.bcit.utils.draw;

import ca.bcit.utils.geom.Vector2F;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DashedDrawing {
	private final static int dashLength = 3;

	public static void drawDashedLine(GraphicsContext gc, Vector2F vec1, Vector2F vec2) {
		drawDashedLine(gc, vec1, vec2, Color.BLACK);
	}

	public static void drawDashedLine(GraphicsContext gc, Vector2F vec1, Vector2F vec2, Color color) {
		gc.setLineWidth(0.75);
		gc.setStroke(color);
		gc.beginPath();
		int dashAmmount = (int) vec1.distance(vec2) / dashLength;
		float xMove = (vec1.getX() - vec2.getX()) / dashAmmount;
		float yMove = (vec1.getY() - vec2.getY()) / dashAmmount;
		boolean on = true;
		gc.moveTo(vec1.getX(), vec1.getY());
		if (dashAmmount % 2 == 1)
			dashAmmount += 1;

		for (int i = 0; i < dashAmmount; i++) {
			if (on)
				gc.lineTo(vec1.getX() - i * xMove, vec1.getY() - i * yMove);
			else
				gc.moveTo(vec1.getX() - i * xMove, vec1.getY() - i * yMove);

			on = !on;
		}

		gc.closePath();
		gc.stroke();
		gc.setLineWidth(1);
	}

	public static void drawDashedRectangle(GraphicsContext gc, Vector2F vec1, Vector2F vec2) {
		float x1 = vec1.getX();
		float y1 = vec1.getY();
		float x2 = vec2.getX();
		float y2 = vec2.getY();
		vec1 = new Vector2F(x1, y1);
		vec2 = new Vector2F(x1, y2);
		drawDashedLine(gc, vec1, vec2, Color.BLACK);
		vec1 = new Vector2F(x2, y1);
		vec2 = new Vector2F(x2, y2);
		drawDashedLine(gc, vec1, vec2, Color.BLACK);
		vec1 = new Vector2F(x1, y2);
		vec2 = new Vector2F(x2, y2);
		drawDashedLine(gc, vec1, vec2, Color.BLACK);
		vec1 = new Vector2F(x1, y1);
		vec2 = new Vector2F(x2, y1);
		drawDashedLine(gc, vec1, vec2, Color.BLACK);
	}

	public static void drawDashedCircle(GraphicsContext gc, Vector2F centerPoint, float radius, Color color) {
		gc.setLineWidth(0.75);
		gc.setStroke(color);
		gc.beginPath();
		int dashAmmount = 36;
		boolean on = false;
		for (int i = 0; i < dashAmmount; i++) {
			Vector2F actualPoint = calculateCirclePoint(centerPoint, radius, dashAmmount, i);
			if (on)
				gc.lineTo((double) actualPoint.getX(), (double) actualPoint.getY());
			else
				gc.moveTo((double) actualPoint.getX(), (double) actualPoint.getY());
			on = !on;
		}
		gc.closePath();
		gc.stroke();
		gc.setLineWidth(1);
	}

	private static Vector2F calculateCirclePoint(Vector2F centerPoint, float radius, int dashAmmount, int actualDash) {
		float x = (float) (centerPoint.getX() + radius * Math.cos(2 * Math.PI * actualDash / dashAmmount));
		float y = (float) (centerPoint.getY() + radius * Math.sin(2 * Math.PI * actualDash / dashAmmount));
		return new Vector2F(x, y);
	}
}
