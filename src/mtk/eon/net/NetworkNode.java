package mtk.eon.net;

import java.util.HashMap;
import java.util.Map;

import mtk.eon.graph.positioned.PositionedNode;
import mtk.eon.io.YamlSerializable;

public class NetworkNode extends PositionedNode implements YamlSerializable {

	String name;
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
	
	@Override
	public String toString() {
		return "{name: " + name + ", replica: " + isReplica + ", regenerators: " + regeneratorsCount + "}";
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	private NetworkNode(Map map) {
		name = (String) map.get("name");
		isReplica = (boolean) map.get("replica");
		regeneratorsCount = (Integer) map.get("regenerators");
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("replica", isReplica);
		map.put("regenerators", regeneratorsCount);
		return map;
	}
}
