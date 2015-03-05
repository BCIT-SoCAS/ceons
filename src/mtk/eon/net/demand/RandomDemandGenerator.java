package mtk.eon.net.demand;

import java.util.Random;

import mtk.eon.net.Network;

public class RandomDemandGenerator extends DemandStream<Demand> {

	Random randomizer;
	int erlang, variance;
	float unicastAnycastRatio;
	
	public RandomDemandGenerator(Network network, long seed, int erlang, int ttlRange, float unicastAnycastRatio) {
		super(network);
		randomizer = new Random(seed);
		this.erlang = erlang;
		this.variance = variance;
		this.unicastAnycastRatio = unicastAnycastRatio;
	}

	@Override
	protected Demand _next() {
		if ()
	}
}
