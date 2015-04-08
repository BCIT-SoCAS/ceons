package mtk.eon.graph.positioned;

import java.util.HashMap;
import java.util.Map;

import mtk.eon.io.YamlSerializable;


public abstract class FixedLengthLink<T extends FixedLengthLink<T>> implements Comparable<T>, YamlSerializable {

	int length;
	
	public FixedLengthLink(int length) {
		this.length = length;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public int compareTo(T other) {
		if (length < other.length) return -1;
		else if (length == other.length) return 0;
		else return 1;
	}
	
	@SuppressWarnings("rawtypes")
	public FixedLengthLink(Map map) {
		length = (Integer) map.get("length");
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("length", length);
		return map;
	}
}
