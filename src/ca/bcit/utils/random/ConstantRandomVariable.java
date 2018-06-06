package ca.bcit.utils.random;

import ca.bcit.io.YamlSerializable;

import java.util.HashMap;
import java.util.Map;

public class ConstantRandomVariable<E> extends RandomVariable<E> implements YamlSerializable {

	private final E value;

	public ConstantRandomVariable(E value) {
		this.value = value;
	}
	
	@Override
	public E next() {
		return value;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ConstantRandomVariable(Map map) {
		value = (E) map.get("value");
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("value", value);
		return map;
	}
}
