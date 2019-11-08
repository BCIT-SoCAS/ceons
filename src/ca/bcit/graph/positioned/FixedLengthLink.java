package ca.bcit.graph.positioned;

import ca.bcit.io.YamlSerializable;

import java.util.HashMap;
import java.util.Map;

public abstract class FixedLengthLink<T extends FixedLengthLink<T>> implements Comparable<T>, YamlSerializable {
	public int length;
	
	protected FixedLengthLink(int length) {
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
		return Integer.compare(length, other.length);
	}
	
	@SuppressWarnings("rawtypes")
	protected FixedLengthLink(Map map) {
		length = (Integer) map.get("length");
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("length", length);
		return map;
	}
}
