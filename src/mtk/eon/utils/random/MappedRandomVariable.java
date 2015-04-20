package mtk.eon.utils.random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mtk.eon.io.YamlSerializable;
import mtk.eon.utils.IntegerRange;

public class MappedRandomVariable<E> extends RandomVariable<E> implements YamlSerializable {
	
	public static class Entry<E> implements YamlSerializable {
		
		private int probability;
		private E value;
		
		public Entry(int probability, E value) {
			this.probability = probability;
			this.value = value;
		}
		
		public E getValue() {
			return value;
		}
		
		public int getProbability() {
			return probability;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Entry(Map map) {
			this((Integer) map.get("probability"), (E) map.get("value"));
		}

		@Override
		public Map<String, Object> serialize() {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("probability", probability);
			map.put("value", value);
			return map;
		}
	}
	
	int upperBound;
	IntegerRange[] ranges;
	List<Entry<E>> entries;
	
	public MappedRandomVariable(List<Entry<E>> distribution) {
		super();
		ranges = new IntegerRange[distribution.size()];
		entries = distribution;
		for (int i = 0; i < ranges.length; i++)
			ranges[i] = new IntegerRange((i > 0 ? ranges[i - 1].getEndOffset() : 0), distribution.get(i).getProbability());
		upperBound = ranges[ranges.length - 1].getEndOffset();
	}
	
	public List<E> values() {
		List<E> values = new ArrayList<E>();
		for (Entry<E> entry : this.entries) values.add(entry.getValue());
		return values;
	}
	
	@Override
	public E next() {
		return entries.get(IntegerRange.binarySearch(ranges, generator.nextInt(upperBound))).getValue();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MappedRandomVariable(Map map) {
		this((List<Entry<E>>) map.get("distribution"));
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("distribution", entries);
		return map;
	}
}
