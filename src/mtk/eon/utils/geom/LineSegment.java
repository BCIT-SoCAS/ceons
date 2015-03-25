package mtk.eon.utils.geom;

import mtk.eon.drawing.Link;

public class LineSegment {
	private Vector2F startPoint;
	private Vector2F endPoint;
	public LineSegment(Vector2F vec1,Vector2F vec2)
	{
		startPoint=vec1;
		endPoint=vec2;
	}
	public boolean areCrossing(LineSegment line) {
		Vector2F directional1 = endPoint.subtract(startPoint);
		Vector2F directional2 = line.endPoint.subtract(line.startPoint);
		if (crossProduct(directional1, directional2) == 0) return false;
		float multiplier1 = crossProduct(line.startPoint.subtract(startPoint), directional2.multiply(1f / crossProduct(directional1, directional2)));
		float multiplier2 = crossProduct(startPoint.subtract(line.startPoint), directional1.multiply(1f / crossProduct(directional2, directional1)));
		return multiplier1 >= 0f && multiplier1 <= 1f && multiplier2 >= 0f && multiplier2 <= 1f;
	}
	public boolean areCrossing(Link link)
	{
		LineSegment line=new LineSegment(link.getStartPoint(),link.getEndPoint());
		return areCrossing(line);
	}
	
	private float crossProduct(Vector2F vector1, Vector2F vector2) {
		return vector1.getX() * vector2.getY() - vector1.getY() * vector2.getX();
	}
}
