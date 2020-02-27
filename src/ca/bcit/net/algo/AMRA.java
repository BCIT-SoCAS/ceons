package ca.bcit.net.algo;

import ca.bcit.net.*;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;
import ca.bcit.net.modulation.IModulation;
import ca.bcit.net.spectrum.NoSpectrumAvailableException;

import java.util.List;

public class AMRA extends BaseRMSAAlgorithm implements IRMSAAlgorithm{
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
		try {
			int volume = (int) Math.ceil(demand.getVolume() / 10.0) - 1;
			List<PartedPath> candidatePaths = demand.getCandidatePaths(false, network);

			rankCandidatePaths(network, volume, candidatePaths);

			allocateWorkingPath(demand, candidatePaths);

			if (shouldAllocateBackupPath(demand, candidatePaths)) {
				int backupVolume = (int) Math.ceil(demand.getSqueezedVolume() / 10.0) - 1;

				rankCandidatePaths(network, backupVolume, demand.getCandidatePaths(true, network));

				allocateBackupPath(demand, candidatePaths);
			}

			return new DemandAllocationResult(demand);
		}
		catch (NoSpectrumAvailableException e) {
			return DemandAllocationResult.NO_SPECTRUM;
		}
		catch (NoRegeneratorsAvailableException | NetworkException e) {
			return DemandAllocationResult.NO_REGENERATORS;
		}
	}

	protected void applyMetricsToCandidatePaths(Network network, int volume, List<PartedPath> candidatePaths) {
		pathLoop: for (PartedPath path : candidatePaths) {
			path.mergeRegeneratorlessParts();

			// choosing modulations for parts
			for (PathPart part : path) {
				for (IModulation modulation : network.getAllowedModulations())
					if (modulation.getMaximumDistanceSupportedByBitrateWithJumpsOfTenGbps()[volume] >= part.getLength())
						part.setModulationIfBetter(modulation, calculateModulationMetric(network, part, modulation));

				if (part.getModulation() == null)
					continue pathLoop;
			}
			path.calculateMetricFromParts();
			path.mergeIdenticalModulation(volume);

			// Unify modulations if needed
			if (!network.canSwitchModulation()) {
				IModulation modulation = path.getModulationFromLongestPart();
				for (PathPart part : path)
					part.setModulation(modulation, calculateModulationMetric(network, part, modulation));
				path.calculateMetricFromParts();
			}

			// Update metrics
			int increment = network.getRegeneratorMetricValue() * path.getNeededRegeneratorsCount();
			path.setMetric(path.getMetric() + increment);
		}
	}

	protected void filterCandidatePaths(List<PartedPath> candidatePaths) {
		for (int i = 0; i < candidatePaths.size(); i++)
			if (candidatePaths.get(i).getMetric() < 0) {
				candidatePaths.remove(i);
				i--;
			}
	}

	private static int calculateModulationMetric(Network network, PathPart part, IModulation modulation) {
		double slicesOccupationPercentage = part.getOccupiedSlicesPercentage() * 100;

		return network.getDynamicModulationMetric(modulation, getSlicesOccupationMetric(slicesOccupationPercentage));
	}

	private static int getSlicesOccupationMetric(double slicesOccupationPercentage) {
		if (slicesOccupationPercentage > 90)
			return 5;
		else if (slicesOccupationPercentage > 75)
			return 4;
		else if (slicesOccupationPercentage > 60)
			return 3;
		else if (slicesOccupationPercentage > 40)
			return 2;
		else if (slicesOccupationPercentage > 20)
			return 1;

		return 0;
	}
}
