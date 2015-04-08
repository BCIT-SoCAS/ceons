package mtk.eon.utils.random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class UniformRandomVariable<E> extends RandomVariable<E> {

	public UniformRandomVariable(long seed) {
		super(seed);
	}

	public static class Object<T> extends UniformRandomVariable<T> {

		List<T> objects;
		
		public Object(long seed, Collection<T> objects) {
			super(seed);
			this.objects = new ArrayList<T>(objects);
		}

		@Override
		public T next() {
			return objects.get(generator.nextInt(objects.size()));
		}
	}
}
