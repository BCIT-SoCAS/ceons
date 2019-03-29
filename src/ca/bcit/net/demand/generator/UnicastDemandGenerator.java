package ca.bcit.net.demand.generator;

import ca.bcit.net.NetworkNode;
import ca.bcit.net.demand.UnicastDemand;
import ca.bcit.utils.random.RandomVariable;

import java.util.Map;
import java.util.Random;

public class UnicastDemandGenerator extends DemandGenerator<UnicastDemand> {
	
	private final RandomVariable<NetworkNode> source;
	private final RandomVariable<NetworkNode> destination;

	public UnicastDemandGenerator(RandomVariable<NetworkNode> source, RandomVariable<NetworkNode> destination, RandomVariable<Boolean> reallocate, RandomVariable<Boolean> allocateBackup, 
			RandomVariable<Integer> volume, RandomVariable<Float> squeezeRatio) {
		super(reallocate, allocateBackup, volume, squeezeRatio);
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
		int i = 0;
		NetworkNode source = this.source.next();
		NetworkNode destination = this.destination.next();
		while (source.equals(destination)){
			destination = this.destination.next();
			i++;
			if (i > 3) {
				source = this.source.next();
			}
		}
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
