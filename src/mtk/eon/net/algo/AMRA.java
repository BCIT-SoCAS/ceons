package mtk.eon.net.algo;

import java.util.ArrayList;
import java.util.Collections;

import mtk.eon.net.MetricType;
import mtk.eon.net.Modulation;
import mtk.eon.net.Network;
import mtk.eon.net.PartedPath;
import mtk.eon.net.PathPart;
import mtk.eon.net.demand.Demand;
import mtk.eon.net.demand.DemandAllocationResult;

public class AMRA extends Algorithm {

	@Override
	public String getName() {
		return "AMRA";
	}
	
	@Override
	public DemandAllocationResult allocateDemand(Demand demand, Network network) {
		ArrayList<PartedPath> candidatePaths = demand.getCandidatePaths(network);
		int volume = (int) Math.ceil(demand.getVolume() / 10) - 1;
		
		pathLoop: for (PartedPath path : candidatePaths) {
			path.mergeRegeneratorlessParts();
			
			// choosing modulations for parts
			for (PathPart part : path) {
				for (Modulation modulation : network.getAllowedModulations())
					if (network.getModulationDistance(modulation, volume) >= part.getLength()) {
						part.setModulationIfBetter(modulation, calculateModulationMetric(network, part, modulation));
					}
				
				if (part.getModulation() == null) continue pathLoop;
			}
			path.calculateMetricFromParts();
			path.mergeIdenticalModulation(network, volume);

			// Unify modulations if needed
			if (!network.canSwitchModulation()) {
				Modulation modulation = path.getModulationFromLongestPart();
				for (PathPart part : path)
					part.setModulation(modulation, calculateModulationMetric(network, part, modulation));
				path.calculateMetricFromParts();
			}
			
			// Update metrics
			path.setMetric(network.getRegeneratorMetricValue() * (network.getRegeneratorMetricType() == MetricType.STATIC ? 
					path.getNeededRegeneratorsCount() : (Math.floor(path.getOccupiedRegeneratorsPercentage() * 10) + 1)) 
					+ path.getMetric());
		}
		Collections.sort(candidatePaths);
		for (int i = 0; i < candidatePaths.size(); i++) if (candidatePaths.get(i).getMetric() < 0) {
			candidatePaths.remove(i);
			i--;
		}
		
		if (candidatePaths.isEmpty()) return DemandAllocationResult.NO_REGENERATORS;
		
		DemandAllocationResult result = null;
		for (PartedPath path : candidatePaths) {
			result = demand.allocate(network, path);
			if (result != DemandAllocationResult.NO_SPECTRUM) return result;
		}
		
		return result;
	}
	
	public int calculateModulationMetric(Network network, PathPart part, Modulation modulation) {
		int metric;
		if (network.getModualtionMetricType() == MetricType.STATIC)
			metric = network.getStaticModulationMetric(modulation);
		else {
			double slicesOccupationPercentage = part.getOccupiedSlicesPercentage() * 100;
			int slicesOccupationMetric;
			if (slicesOccupationPercentage <= 90)
				if (slicesOccupationPercentage <= 75)
					if (slicesOccupationPercentage <= 60)
						if (slicesOccupationPercentage <= 40)
							if (slicesOccupationPercentage <= 20) slicesOccupationMetric = 0;
							else slicesOccupationMetric = 1;
						else slicesOccupationMetric = 2;
					else slicesOccupationMetric = 3;
				else slicesOccupationMetric = 4;
			else slicesOccupationMetric = 5;
			metric = network.getDynamicModulationMetric(modulation, slicesOccupationMetric);
		}
		return metric;
	}
}
