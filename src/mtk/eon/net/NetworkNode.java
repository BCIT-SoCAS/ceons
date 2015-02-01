package mtk.eon.net;

import java.awt.geom.Point2D;

import mtk.general.Identifiable;

public class NetworkNode extends Identifiable {

	String name;
	Point2D.Double position;
	boolean isReplica;
	int regeneratorsCount, occupiedRegenerators;
	
	public NetworkNode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isReplica() {
		return isReplica;
	}
	
	public void setRegeneratorsCount(int regeneratorsCount) {
		this.regeneratorsCount = regeneratorsCount;
	}
	
	public int getFreeRegenerators() {
		return regeneratorsCount - occupiedRegenerators;
	}
	
	public boolean hasFreeRegenerators() {
		return regeneratorsCount - occupiedRegenerators > 0;
	}
	
	public void occupyRegenerators(int count) {
		occupiedRegenerators += count;
		if (occupiedRegenerators > regeneratorsCount || occupiedRegenerators < 0)
			throw new NetworkException("Regenerators occupation exception! (" + occupiedRegenerators + "/" +
					regeneratorsCount + ")");
	}
	
	public void setPosition(int x, int y) {
		if (position == null) position = new Point2D.Double();
		position.setLocation(x, y);
	}
	
	public Point2D.Double getPosition() {
		return position;
	}
}
