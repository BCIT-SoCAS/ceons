package mtk.eon.net.demand.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mtk.eon.io.YamlSerializable;
import mtk.eon.net.demand.AnycastDemand;
import mtk.eon.net.demand.Demand;
import mtk.eon.net.demand.DemandStream;
import mtk.eon.utils.random.MappedRandomVariable;

public class TrafficGenerator implements DemandStream<Demand>, YamlSerializable {

	int generatedDemandsCount;
	String name;
	MappedRandomVariable<DemandGenerator<?>> generators;
	
	DemandGenerator<?> lastAnycast;
	
	public TrafficGenerator(String name, MappedRandomVariable<DemandGenerator<?>> generators) {
		this.name = name;
		this.generators = generators;
	}
	
	public String getName() {
		return name;
	}
	
	public void setSeed(long seed) {
		Random seedGenerator = new Random(seed);
		generators.setSeed(seedGenerator.nextLong());
		for (DemandGenerator<?> generator : generators.values())
			generator.setSeed(seedGenerator.nextLong());
		generatedDemandsCount = 0;
	}
	
	@Override
	public Demand next() { // TODO ;_;
		Demand demand;
		if (lastAnycast == null) {
			DemandGenerator<?> generator = generators.next();
			demand = generator.next();
			if (demand instanceof AnycastDemand) lastAnycast = generator;
			generatedDemandsCount++;
		} else {
			demand = lastAnycast.next();
			lastAnycast = null;
		}
		return demand;
	}

	@Override
	public int getGeneratedDemandsCount() {
		return generatedDemandsCount;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public TrafficGenerator(Map map) {
		name = (String) map.get("name");
		generators = (MappedRandomVariable<DemandGenerator<?>>) map.get("generators");
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("name", name);
		map.put("generators", generators);
		
		return map;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
