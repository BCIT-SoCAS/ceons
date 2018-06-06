package ca.bcit.utils.random;

import ca.bcit.io.YamlSerializable;

import java.util.HashMap;
import java.util.Map;


public abstract class IrwinHallRandomVariable<N> extends RandomVariable<N> {
	
	final int rank;
	
	IrwinHallRandomVariable(int rank) {
		super();
		this.rank = rank;
	}
	
	public IrwinHallRandomVariable(long seed, int rank) {
		super(seed);
		this.rank = rank;
	}

	public static class Integer extends IrwinHallRandomVariable<java.lang.Integer> implements YamlSerializable {

		int offset, width;
		
		public Integer(int min, int max, int rank) {
			super(rank);
			offset = min;
			width = max - min;
		}

		@Override
		public java.lang.Integer next() {
			int result = 0;
			for (int i = 0; i < rank; i++) result += generator.nextInt(width);
			return result / rank + offset;
		}
		
		@SuppressWarnings("rawtypes")
		public Integer(Map map) {
			super((int) map.get("rank"));
			offset = (int) map.get("offset");
			offset = (int) map.get("width");
		}
		
		@Override
		public Map<String, Object> serialize() {
			Map<String, Object> map = new HashMap<>();

			map.put("rank", rank);
			map.put("offset", offset);
			map.put("width", width);
			
			return map;
		}
	}
}
