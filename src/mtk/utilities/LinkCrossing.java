package mtk.utilities;

import mtk.eon.drawing.Link;
import mtk.geom.Vector2F;

public class LinkCrossing {

	Link link1;
	Link link2;

	public LinkCrossing(Link _link1, Link _link2) {
		link1 = _link1;
		link2 = _link2;
	}

	public boolean areCrossing() {
		Vector2F directional1 = link1.getEndPoint().subtract(link1.getStartPoint());
		Vector2F directional2 = link2.getEndPoint().subtract(link2.getStartPoint());
		if (crossProduct(directional1, directional2) == 0) return false;
		float multiplier1 = crossProduct(link2.getStartPoint().subtract(link1.getStartPoint()), directional2.multiply(1f / crossProduct(directional1, directional2)));
		float multiplier2 = crossProduct(link1.getStartPoint().subtract(link2.getStartPoint()), directional1.multiply(1f / crossProduct(directional2, directional1)));
		return multiplier1 >= 0f && multiplier1 <= 1f && multiplier2 >= 0f && multiplier2 <= 1f;
	}
	
	private float crossProduct(Vector2F vector1, Vector2F vector2) {
		return vector1.getX() * vector2.getY() - vector1.getY() * vector2.getX();
	}
}
