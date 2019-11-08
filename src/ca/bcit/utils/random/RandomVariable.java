package ca.bcit.utils.random;

import java.util.Random;

public abstract class RandomVariable<E> {
	Random generator;
	
	RandomVariable() {}
	
	RandomVariable(long seed) {
		generator = new Random(seed);
	}
	
	public void setSeed(long seed) {
		generator = new Random(seed);
	}
	
	public abstract E next();
}
