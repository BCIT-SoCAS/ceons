package mtk.eon.net;

import java.util.HashMap;
import java.util.Map;

import mtk.eon.graph.positioned.PositionedNode;
import mtk.eon.io.YamlSerializable;

public class NetworkNode extends PositionedNode implements YamlSerializable {

	String name;
	int regeneratorsCount, occupiedRegenerators;
	
	public NetworkNode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
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
	public boolean equals(Object o) {
		return o instanceof NetworkNode && ((NetworkNode) o).getName().equals(name);
	}
	
	@Override
	public String toString() {
		return "{name: " + name + ", regenerators: " + regeneratorsCount + "}";
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	private NetworkNode(Map map) {
		name = (String) map.get("name");
		regeneratorsCount = (Integer) map.get("regenerators");
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("regenerators", regeneratorsCount);
		return map;
	}
}
