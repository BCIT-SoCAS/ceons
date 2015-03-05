package mtk.graph.positioned;

import mtk.general.Identifiable;
import mtk.geom.Vector2F;

public abstract class PositionedNode extends Identifiable {
	
	Vector2F position;
	
	public void setPosition(Vector2F position) {
		this.position = position;
	}
	
	public void setPosition(float x, float y) {
		this.position = new Vector2F(x, y);
	}
	
	public Vector2F getPosition() {
		return position;
	}
}
