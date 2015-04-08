package mtk.eon.net.demand.generator;

import mtk.eon.net.demand.Demand;
import mtk.eon.net.demand.DemandStream;
import mtk.eon.utils.random.MappedRandomVariable;

public class TrafficGenerator implements DemandStream<Demand> {

	int generatedDemandsCount;
	MappedRandomVariable<DemandStream<? extends Demand>> generators;
	
	public TrafficGenerator(MappedRandomVariable<DemandStream<? extends Demand>> generators) {
		this.generators = generators;
	}
	
	@Override
	public Demand next() {
		generatedDemandsCount++;
		return generators.next().next();
	}

	@Override
	public int getGeneratedDemandsCount() {
		return generatedDemandsCount;
	}
}
