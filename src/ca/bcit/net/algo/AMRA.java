package ca.bcit.net.algo;

import ca.bcit.net.*;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;

import java.util.Collections;
import java.util.List;

public class AMRA implements IRMSAAlgorithm{
	public String getKey(){
		return "AMRA";
	};

	public String getName(){
		return "AMRA";
	};

	public String getDocumentationURL(){
		return "https://pubsonline.informs.org/doi/pdf/10.1287/opre.24.6.1164";
	};

	@Override
	public DemandAllocationResult allocateDemand(Demand demand, Network network) {
		int volume = (int) Math.ceil(demand.getVolume() / 10) - 1;

		List<PartedPath> candidatePaths = demand.getCandidatePaths(false, network);
		if (candidatePaths.isEmpty())
			return DemandAllocationResult.NO_SPECTRUM;

		candidatePaths = applyMetrics(network, volume, candidatePaths);

		if (candidatePaths.isEmpty())
			return DemandAllocationResult.NO_REGENERATORS;

		boolean workingPathSuccess = false;

		try {
			for (PartedPath path : candidatePaths)
				if (demand.allocate(network, path)) {
					workingPathSuccess = true;
					break;
				}

		}
		catch (NetworkException storage) {
			workingPathSuccess = false;
			return DemandAllocationResult.NO_REGENERATORS;
		}
		if (!workingPathSuccess)
			return DemandAllocationResult.NO_SPECTRUM;

		try {
			if (demand.allocateBackup()) {
				volume = (int) Math.ceil(demand.getSqueezedVolume() / 10) - 1;

				candidatePaths = applyMetrics(network, volume, demand.getCandidatePaths(true, network));

				if (candidatePaths.isEmpty())
					return new DemandAllocationResult(
							demand.getWorkingPath());
				for (PartedPath path : candidatePaths)
					if (demand.allocate(network, path))
						return new DemandAllocationResult(demand.getWorkingPath(), demand.getBackupPath());

				return new DemandAllocationResult(demand.getWorkingPath());
			}
		}
		catch (NetworkException e) {
			workingPathSuccess = false;
			return DemandAllocationResult.NO_REGENERATORS;
		}

		return new DemandAllocationResult(demand.getWorkingPath());
	}

	private static List<PartedPath> applyMetrics(Network network, int volume, List<PartedPath> candidatePaths) {
		pathLoop: for (PartedPath path : candidatePaths) {
			path.mergeRegeneratorlessParts();

			// choosing modulations for parts
			for (PathPart part : path) {
				for (Modulation modulation : network.getAllowedModulations())
					if (modulation.modulationDistances[volume] >= part.getLength())
						part.setModulationIfBetter(modulation, calculateModulationMetric(network, part, modulation));

				if (part.getModulation() == null)
					continue pathLoop;
			}
			path.calculateMetricFromParts();
			path.mergeIdenticalModulation(volume);

			// Unify modulations if needed
			if (!network.canSwitchModulation()) {
				Modulation modulation = path.getModulationFromLongestPart();
				for (PathPart part : path)
					part.setModulation(modulation, calculateModulationMetric(network, part, modulation));
				path.calculateMetricFromParts();
			}

			// Update metrics
			path.setMetric(
					network.getRegeneratorMetricValue()
							* (path.getNeededRegeneratorsCount())
							+ path.getMetric());
		}
		Collections.sort(candidatePaths);
		for (int i = 0; i < candidatePaths.size(); i++)
			if (candidatePaths.get(i).getMetric() < 0) {
				candidatePaths.remove(i);
				i--;
			}

		return candidatePaths;
	}

	private static int calculateModulationMetric(Network network, PathPart part, Modulation modulation) {
		double slicesOccupationPercentage = part.getOccupiedSlicesPercentage() * 100;
		int slicesOccupationMetric;

		if (slicesOccupationPercentage <= 90)
			if (slicesOccupationPercentage <= 75)
				if (slicesOccupationPercentage <= 60)
					if (slicesOccupationPercentage <= 40)
						if (slicesOccupationPercentage <= 20)
							slicesOccupationMetric = 0;
						else
							slicesOccupationMetric = 1;
					else
						slicesOccupationMetric = 2;
				else
					slicesOccupationMetric = 3;
			else
				slicesOccupationMetric = 4;
		else
			slicesOccupationMetric = 5;

		return network.getDynamicModulationMetric(modulation, slicesOccupationMetric);
	}
}
