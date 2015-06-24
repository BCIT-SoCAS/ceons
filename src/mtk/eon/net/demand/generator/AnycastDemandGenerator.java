package mtk.eon.net.demand.generator;

import java.util.Map;
import java.util.Random;

import mtk.eon.net.NetworkNode;
import mtk.eon.net.demand.AnycastDemand;
import mtk.eon.utils.random.RandomVariable;

public class AnycastDemandGenerator extends DemandGenerator<AnycastDemand> {

	private RandomVariable<NetworkNode> client;
	private boolean replicaPreservation;
	
	private AnycastDemand downstream;
	
	public AnycastDemandGenerator(RandomVariable<NetworkNode> client, RandomVariable<Boolean> reallocate, RandomVariable<Boolean> allocateBackup, 
			RandomVariable<Integer> volume, RandomVariable<Float> squeezeRatio) {
		super(reallocate, allocateBackup, volume, squeezeRatio);
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
			downstream = new AnycastDemand.Downstream(client, reallocate, allocateBackup, volume.next(), squeezeRatio.next(), ttl, replicaPreservation);
			result = new AnycastDemand.Upstream(client, reallocate, allocateBackup, volume.next(), squeezeRatio.next(), ttl, replicaPreservation);
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
