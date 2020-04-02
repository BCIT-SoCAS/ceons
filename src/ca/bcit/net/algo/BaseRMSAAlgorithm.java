package ca.bcit.net.algo;

import ca.bcit.net.*;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;
import ca.bcit.net.spectrum.NoSpectrumAvailableException;

import java.util.Collections;
import java.util.List;

public abstract class BaseRMSAAlgorithm{
	public DemandAllocationResult allocateDemand(Demand demand, Network network) throws InstantiationException, ClassNotFoundException, IllegalAccessException {
		try {
			int volume = (int) Math.ceil(demand.getVolume() / 10.0) - 1;
			List<PartedPath> candidatePaths = demand.getCandidatePaths(false, network);

			rankCandidatePaths(network, volume, candidatePaths);

			allocateWorkingPath(demand, candidatePaths);

			if (shouldAllocateBackupPath(demand, candidatePaths))
				allocateBackupPath(demand, candidatePaths);

			return new DemandAllocationResult(demand);
		}
		catch (NoSpectrumAvailableException e) {
			return DemandAllocationResult.NO_SPECTRUM;
		}
		catch (NoRegeneratorsAvailableException | NetworkException e) {
			return DemandAllocationResult.NO_REGENERATORS;
		}
	}

	protected void rankCandidatePaths(Network network, int volume, List<PartedPath> candidatePaths) {
		if (candidatePaths.isEmpty())
			throw new NoSpectrumAvailableException("There are no candidate paths to allocate the demand.");

		applyMetricsToCandidatePaths(network, volume, candidatePaths);

		filterCandidatePaths(candidatePaths);

		if (candidatePaths.isEmpty())
			throw new NoRegeneratorsAvailableException("There are no candidate paths to allocate the demand.");

		sortCandidatePaths(candidatePaths);
	}

	protected boolean allocateToHighestRankedPathAvailable(Demand demand, List<PartedPath> candidatePaths) throws NetworkException {
		for (PartedPath path : candidatePaths)
			if (demand.allocate(path))
				return true;

		return false;
	}

	protected void sortCandidatePaths(List<PartedPath> candidatePaths) {
		Collections.sort(candidatePaths);
	}

	protected void filterCandidatePaths(List<PartedPath> candidatePaths) {}

	protected boolean shouldAllocateBackupPath(Demand demand, List<PartedPath> candidatePaths) {
		return demand.allocateBackup() && !candidatePaths.isEmpty();
	}

	protected void allocateWorkingPath(Demand demand, List<PartedPath> candidatePaths) {
		allocateToHighestRankedPathAvailable(demand, candidatePaths);
	}

	protected void allocateBackupPath(Demand demand, List<PartedPath> candidatePaths) {
		allocateToHighestRankedPathAvailable(demand, candidatePaths);
	}

	protected abstract void applyMetricsToCandidatePaths(Network network, int volume, List<PartedPath> candidatePaths);
}
