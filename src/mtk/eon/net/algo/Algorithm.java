package mtk.eon.net.algo;

import java.util.Collection;
import java.util.HashMap;

import mtk.eon.net.Network;
import mtk.eon.net.demand.Demand;
import mtk.eon.net.demand.DemandAllocationResult;

public abstract class Algorithm {
	
	static HashMap<String, Algorithm> registeredAlgorithms;
	
	static {
		registeredAlgorithms = new HashMap<String, Algorithm>();
		registerAlgorithm(new AMRA());
		registerAlgorithm(new MNC());
	}
	
	public static void registerAlgorithm(Algorithm algorithm) {
		if (!registeredAlgorithms.containsKey(algorithm.getName()))
			registeredAlgorithms.put(algorithm.getName(), algorithm);
	}
	
	public static Algorithm getAlgorithmByName(String name) {
		return registeredAlgorithms.get(name);
	}
	
	public static Collection<Algorithm> getRegisteredAlgorithms() {
		return registeredAlgorithms.values();
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public abstract String getName();
	
	public abstract DemandAllocationResult allocateDemand(Demand demand, Network network);
}
