package mtk.eon.utils.random;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.Map.Entry;

import mtk.eon.utils.IntegerRange;

public class MappedRandomVariable<E> extends RandomVariable<E> {
	
	int upperBound;
	IntegerRange[] ranges;
	E[] values;
	
	@SuppressWarnings("unchecked")
	public MappedRandomVariable(long seed, Map<E, Integer> distribution, Class<E> clazz) {
		super(seed);
		ranges = new IntegerRange[distribution.size()];
		values = (E[]) Array.newInstance(clazz, distribution.size());
		int i = 0;
		for (Entry<E, Integer> entry : distribution.entrySet()) {
			ranges[i] = new IntegerRange((i > 0 ? ranges[i - 1].getEndOffset() : 0), entry.getValue());
			values[i] = entry.getKey();
			i++;
		}
		upperBound = ranges[ranges.length - 1].getEndOffset();
	}
	
	@Override
	public E next() {
		return values[IntegerRange.binarySearch(ranges, generator.nextInt(upperBound))];
	}
}
