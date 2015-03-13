package mtk.eon.net.algo;

import java.util.Collection;
import java.util.HashMap;

import mtk.eon.net.Network;
import mtk.eon.net.demand.Demand;
import mtk.eon.net.demand.DemandAllocationResult;

public abstract class RMSAAlgorithm {
	
	static HashMap<String, RMSAAlgorithm> registeredAlgorithms = new HashMap<String, RMSAAlgorithm>();
	
	static {
		registerAlgorithm(new AMRA());
		registerAlgorithm(new MNC());
	}
	
	public static void registerAlgorithm(RMSAAlgorithm algorithm) {
		if (!registeredAlgorithms.containsKey(algorithm.getName()))
			registeredAlgorithms.put(algorithm.getName(), algorithm);
	}
	
	public static RMSAAlgorithm getAlgorithmByName(String name) {
		return registeredAlgorithms.get(name);
	}
	
	public static Collection<RMSAAlgorithm> getRegisteredAlgorithms() {
		return registeredAlgorithms.values();
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public abstract String getName();
	
	public abstract DemandAllocationResult allocateDemand(Demand demand, Network network);
}
