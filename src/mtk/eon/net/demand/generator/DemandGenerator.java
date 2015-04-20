package mtk.eon.net.demand.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mtk.eon.io.YamlSerializable;
import mtk.eon.net.demand.Demand;
import mtk.eon.net.demand.DemandStream;
import mtk.eon.utils.random.RandomVariable;

public abstract class DemandGenerator<D extends Demand> implements DemandStream<D>, YamlSerializable {

	protected RandomVariable<Boolean> reallocate, allocateBackup;
	protected RandomVariable<Integer> volume, ttl;
	protected RandomVariable<Float> squeezeRatio;

	protected int generatedDemandsCount;
	
	public DemandGenerator(RandomVariable<Boolean> reallocate, RandomVariable<Boolean> allocateBackup, RandomVariable<Integer> volume, RandomVariable<Integer> ttl, RandomVariable<Float> squeezeRatio) {
		this.reallocate = reallocate;
		this.allocateBackup = allocateBackup;
		this.volume = volume;
		this.ttl = ttl;
		this.squeezeRatio = squeezeRatio;
	}
	
	public Random setSeed(long seed) {
		Random seedGenerator = new Random(seed);
		reallocate.setSeed(seedGenerator.nextLong());
		allocateBackup.setSeed(seedGenerator.nextLong());
		volume.setSeed(seedGenerator.nextLong());
		squeezeRatio.setSeed(seedGenerator.nextLong());
		ttl.setSeed(seedGenerator.nextLong());
		return seedGenerator;
	}

	@Override
	public int getGeneratedDemandsCount() {
		return generatedDemandsCount;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DemandGenerator(Map map) {
		reallocate = (RandomVariable<Boolean>) map.get("reallocate");
		allocateBackup = (RandomVariable<Boolean>) map.get("allocateBackup");
		volume = (RandomVariable<Integer>) map.get("volume");
		squeezeRatio = (RandomVariable<Float>) map.get("squeezeRatio");
		ttl = (RandomVariable<Integer>) map.get("ttl");
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("reallocate", reallocate);
		map.put("allocateBackup", allocateBackup);
		map.put("volume", volume);
		map.put("squeezeRatio", squeezeRatio);
		map.put("ttl", ttl);
		
		return map;
	}
}
