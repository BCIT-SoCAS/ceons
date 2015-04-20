package mtk.eon.net.demand.generator;

import java.util.Map;
import java.util.Random;

import mtk.eon.net.NetworkNode;
import mtk.eon.net.demand.UnicastDemand;
import mtk.eon.utils.random.RandomVariable;

public class UnicastDemandGenerator extends DemandGenerator<UnicastDemand> {
	
	RandomVariable<NetworkNode> source, destination;

	public UnicastDemandGenerator(RandomVariable<NetworkNode> source, RandomVariable<NetworkNode> destination, RandomVariable<Boolean> reallocate, RandomVariable<Boolean> allocateBackup, 
			RandomVariable<Integer> volume, RandomVariable<Integer> ttl, RandomVariable<Float> squeezeRatio) {
		super(reallocate, allocateBackup, volume, ttl, squeezeRatio);
		this.source = source;
		this.destination = destination;
	}
	
	@Override
	public Random setSeed(long seed) {
		Random seedGenerator = super.setSeed(seed);
		source.setSeed(seedGenerator.nextLong());
		destination.setSeed(seedGenerator.nextLong());
		return seedGenerator;
	}
	
	@Override
	public UnicastDemand next() {
		NetworkNode source = this.source.next(), destination;
		do destination = this.destination.next(); while (source.equals(destination));
		generatedDemandsCount++;
		return new UnicastDemand(source, destination, reallocate.next(), allocateBackup.next(), volume.next(), squeezeRatio.next(), ttl.next());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public UnicastDemandGenerator(Map map) {
		super(map);
		source = (RandomVariable<NetworkNode>) map.get("source");
		destination = (RandomVariable<NetworkNode>) map.get("destination");
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("source", source);
		map.put("destination", destination);
		return map;
	}
}
