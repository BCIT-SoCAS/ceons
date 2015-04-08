package mtk.eon.net.demand.generator;

import mtk.eon.graph.Relation;
import mtk.eon.net.NetworkNode;
import mtk.eon.net.demand.Demand;
import mtk.eon.net.demand.DemandStream;
import mtk.eon.net.demand.UnicastDemand;
import mtk.eon.utils.random.RandomVariable;

public class UnicastDemandGenerator implements DemandStream<Demand> {
	
	int generatedDemandsCount;
	RandomVariable<Integer> initVolume, minVolume, ttl;
	
	public UnicastDemandGenerator(RandomVariable<Relation<NetworkNode, ?, ?>> relations,
			RandomVariable<Integer> initVolume, RandomVariable<Integer> minVolume, RandomVariable<Integer> ttl) {
		this.initVolume = initVolume;
		this.minVolume = minVolume;
		this.ttl = ttl;
	}
	
	@Override
	public Demand next() {
		generatedDemandsCount++;
		return new UnicastDemand(null, null, initVolume.next(), minVolume.next(), ttl.next());
	}
	
	@Override
	public int getGeneratedDemandsCount() {
		return generatedDemandsCount;
	}
}
