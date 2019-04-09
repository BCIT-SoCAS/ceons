package ca.bcit.net.algo;

import ca.bcit.net.Network;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;

import java.util.Collection;
import java.util.HashMap;

public abstract class RMSAAlgorithm {
	
	private static final HashMap<String, RMSAAlgorithm> registeredAlgorithms = new HashMap<>();
	
	static {
		registerAlgorithm(new SPF());
		registerAlgorithm(new AMRA());
	}
	
	private static void registerAlgorithm(RMSAAlgorithm algorithm) {
		if (!registeredAlgorithms.containsKey(algorithm.getName()))
			registeredAlgorithms.put(algorithm.getName(), algorithm);
	}
	
	public static Collection<RMSAAlgorithm> getRegisteredAlgorithms() {
		return registeredAlgorithms.values();
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	protected abstract String getName();
	
	public abstract DemandAllocationResult allocateDemand(Demand demand, Network network);
}
