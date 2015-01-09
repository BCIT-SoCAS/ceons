package mtk.eon.net.algo;

import java.util.ArrayList;
import java.util.List;

import mtk.eon.net.Demand;
import mtk.eon.net.DemandAllocationResult;
import mtk.eon.net.Network;

public abstract class Algorithm {
	
	static List<Algorithm> registeredAlgorithms;
	
	static {
		registeredAlgorithms = new ArrayList<Algorithm>();
		registerAlgorithm(new AlgorithmA());
	}
	
	public static void registerAlgorithm(Algorithm algorithm) {
		if (!registeredAlgorithms.contains(algorithm))
			registeredAlgorithms.add(algorithm);
	}
	
	public static Algorithm getAlgorithmByID(int algorithmID) {
		return registeredAlgorithms.get(algorithmID);
	}
	
	public abstract DemandAllocationResult allocateDemand(Demand demand, Network network);
}
