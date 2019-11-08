package ca.bcit.graph.positioned;

import ca.bcit.utils.collections.Identifiable;
import ca.bcit.utils.geom.Vector2F;

public abstract class PositionedNode extends Identifiable {
	private Vector2F position;

	public void setPosition(float x, float y) {
		this.position = new Vector2F(x, y);
	}
	
	public Vector2F getPosition() {
		return position;
	}
}
