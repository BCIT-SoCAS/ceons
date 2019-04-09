package ca.bcit.net;

import ca.bcit.graph.positioned.PositionedNode;
import ca.bcit.io.YamlSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Network Node with information about the regenerators
 * 
 * @author Michal
 *
 */
public class NetworkNode extends PositionedNode implements YamlSerializable {

	private final String name;
	int regeneratorsCount, occupiedRegenerators;

	public NetworkNode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void clearOccupied(){
		this.occupiedRegenerators = 0;
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

	public void occupyRegenerators(int count, boolean deallocate) {
		if (deallocate) {
			occupiedRegenerators += count;
		} else if ((count > regeneratorsCount - occupiedRegenerators || occupiedRegenerators < 0) && !deallocate) {
			throw new NetworkException(
					"Regenerators occupation exception! (" + occupiedRegenerators + "/" + regeneratorsCount + ")");
		} else {
			occupiedRegenerators += count;
		}
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
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("regenerators", regeneratorsCount);
		return map;
	}
}
