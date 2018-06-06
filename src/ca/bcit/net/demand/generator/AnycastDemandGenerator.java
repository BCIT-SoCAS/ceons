package ca.bcit.net.demand.generator;

import ca.bcit.net.NetworkNode;
import ca.bcit.net.demand.AnycastDemand;
import ca.bcit.utils.random.RandomVariable;

import java.util.Map;
import java.util.Random;

public class AnycastDemandGenerator extends DemandGenerator<AnycastDemand> {

	private final RandomVariable<NetworkNode> client;
	private boolean replicaPreservation;
	
	private AnycastDemand downstream;
	
	public AnycastDemandGenerator(RandomVariable<NetworkNode> client, RandomVariable<Boolean> reallocate, RandomVariable<Boolean> allocateBackup, 
			RandomVariable<Integer> volume, RandomVariable<Float> squeezeRatio, RandomVariable<Integer> cpu, RandomVariable<Integer> memory, RandomVariable<Integer> storage) {
		super(reallocate, allocateBackup, volume, squeezeRatio, cpu, memory, storage);
		this.client = client;
	}
	
	public void setReplicaPreservation(boolean backupPreservation) {
		this.replicaPreservation = backupPreservation;
	}
	
	@Override
	public Random setSeed(long seed) {
		Random seedGenerator = super.setSeed(seed);
		client.setSeed(seedGenerator.nextLong());
		return seedGenerator;
	}
	
	@Override
	public AnycastDemand next() {
		AnycastDemand result;
		
		if (downstream != null) {
			result = downstream;
			downstream = null;
		} else {
			NetworkNode client = this.client.next();
			boolean reallocate = this.reallocate.next();
			boolean allocateBackup = this.allocateBackup.next();
			int ttl = this.ttl.next();
			int cpu = this.cpu.next();
			int memory = this.memory.next();
			int storage = this.memory.next();
			downstream = new AnycastDemand.Downstream(client, reallocate, allocateBackup, volume.next(), squeezeRatio.next(), ttl, replicaPreservation, cpu, memory, storage);
			result = new AnycastDemand.Upstream(client, reallocate, allocateBackup, volume.next(), squeezeRatio.next(), ttl, replicaPreservation, cpu, memory, storage);
		}
		
		generatedDemandsCount++;
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AnycastDemandGenerator(Map map) {
		super(map);
		client = (RandomVariable<NetworkNode>) map.get("client");
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("client", client);
		return map;
	}
}
