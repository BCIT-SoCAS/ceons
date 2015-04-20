package mtk.eon.utils.random;

import java.util.Random;

public abstract class RandomVariable<E> {

	protected Random generator;
	
	public RandomVariable() {
	}
	
	public RandomVariable(long seed) {
		generator = new Random(seed);
	}
	
	public void setSeed(long seed) {
		generator = new Random(seed);
	}
	
	public abstract E next();
}
