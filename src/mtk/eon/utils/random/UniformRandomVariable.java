package mtk.eon.utils.random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mtk.eon.io.YamlSerializable;

public abstract class UniformRandomVariable<E> extends RandomVariable<E> {

	public UniformRandomVariable() {
		super();
	}
	
	public UniformRandomVariable(long seed) {
		super(seed);
	}

	public static class Generic<T> extends UniformRandomVariable<T> implements YamlSerializable {

		List<T> objects;
		
		public Generic(long seed, Collection<T> objects) {
			super(seed);
			this.objects = new ArrayList<T>(objects);
		}
		
		public Generic(Collection<T> objects) {
			super();
			this.objects = new ArrayList<T>(objects);
		}

		@Override
		public T next() {
			return objects.get(generator.nextInt(objects.size()));
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Generic(Map map) {
			objects = (List<T>) map.get("values");
		}
		
		@Override
		public Map<String, Object> serialize() {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("values", objects);
			return map;
		}
	}
	
	public static class Integer extends UniformRandomVariable<java.lang.Integer> {
		
		int from, to, interval;
		
		public Integer(long seed, int from, int to, int interval) {
			super(seed);
			this.from = from;
			this.to = to;
			this.interval = interval;
		}
		
		public Integer(int from, int to, int interval) {
			super();
			this.from = from;
			this.to = to;
			this.interval = interval;
		}

		@Override
		public java.lang.Integer next() {
			return generator.nextInt((to - from) / interval) * interval + from;
		}
	}
}
