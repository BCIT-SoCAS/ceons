package mtk.eon.utils.random;

import java.util.HashMap;
import java.util.Map;

import mtk.eon.io.YamlSerializable;

public class ConstantRandomVariable<E> extends RandomVariable<E> implements YamlSerializable {

	E value;

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
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("value", value);
		return map;
	}
}
