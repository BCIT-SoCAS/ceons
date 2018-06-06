package ca.bcit.net.algo;

import ca.bcit.net.*;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;
import ca.bcit.net.spectrum.*;
import ca.bcit.utils.Container;

import java.util.Collections;
import java.util.List;

/**
 * Different RMSA algorithm 
 *
 */
public class MNC extends RMSAAlgorithm {

	@Override
	public String getName() {
		return "MNC";
	}

	@SuppressWarnings("unused")
	@Override
	public DemandAllocationResult allocateDemand(Demand demand, Network network) {
		int volume = (int) Math.ceil(demand.getVolume() / 10) - 1;
		List<PartedPath> candidatePaths = demand.getCandidatePaths(false, network);
		if (candidatePaths.isEmpty()) return DemandAllocationResult.NO_SPECTRUM;

		checkModulations(network, volume, candidatePaths);

		Collections.sort(candidatePaths);
		while (candidatePaths.size() > 0 && candidatePaths.get(0).getMetric() < 0) candidatePaths.remove(0);
		
		if (candidatePaths.isEmpty()) return DemandAllocationResult.NO_REGENERATORS;
		
		path: for (PartedPath path : candidatePaths)
			path.setMetric(mnc(network, path, demand));
		Collections.sort(candidatePaths, (p1, p2) -> Double.compare(p2.getMetric(), p1.getMetric()));
		
		boolean workingPathSuccess = false;
		for (PartedPath path : candidatePaths)
			if (demand.allocate(network, path)) {
				workingPathSuccess = true;
				break;
			}
		
		if (!workingPathSuccess) return DemandAllocationResult.NO_SPECTRUM;
		
		if (demand.allocateBackup()) {
			volume = (int) Math.ceil(demand.getSqueezedVolume() / 10) - 1;
			candidatePaths = demand.getCandidatePaths(false, network);
			if (candidatePaths.isEmpty()) return new DemandAllocationResult(demand.getWorkingPath());

			checkModulations(network, volume, candidatePaths);

			Collections.sort(candidatePaths);
			while (candidatePaths.size() > 0 && candidatePaths.get(0).getMetric() < 0) candidatePaths.remove(0);
			
			if (candidatePaths.isEmpty()) return new DemandAllocationResult(demand.getWorkingPath());
			
			path: for (PartedPath path : candidatePaths)
				path.setMetric(mnc(network, path, demand));
			Collections.sort(candidatePaths, (p1, p2) -> Double.compare(p2.getMetric(), p1.getMetric()));
			
			for (PartedPath path : candidatePaths)
				if (demand.allocate(network, path)) return new DemandAllocationResult(demand.getWorkingPath(), demand.getBackupPath());
			
			return new DemandAllocationResult(demand.getWorkingPath());
		}
		
		return new DemandAllocationResult(demand.getWorkingPath());
	}

	private void checkModulations(Network network, int volume, List<PartedPath> candidatePaths) {
		path: for (PartedPath path : candidatePaths) {
			path.mergeRegeneratorlessParts();

			for (PathPart part : path)
				for (Modulation modulation : Modulation.values())
					if (part.getLength() <= modulation.modulationDistances[volume])
						part.setModulation(modulation, 0);
					else if (modulation == Modulation.BPSK)
						continue path;
					else break;

			if (!network.canSwitchModulation()) {
				Modulation modulation = path.getModulationFromLongestPart();
				for (PathPart part : path)
					part.setModulation(modulation, 0);
			}

			path.setMetric(0);
		}
	}

	private double s(Spectrum spectrum1, Spectrum spectrum2, Demand demand) {
		int slices = 0;
		
		if (demand.getWorkingPath() == null) {
			for (SpectrumSegment segment : spectrum1.merge(spectrum2).getSegments())
				if (segment instanceof FreeSpectrumSegment)
					slices += demand.getTTL() * segment.getRange().getLength();
				else if (segment instanceof AllocatableSpectrumSegment) {
					int occupationTimeLeft = ((AllocatableSpectrumSegment) segment).getOccupationTimeLeft();
					if (occupationTimeLeft < demand.getTTL())
						slices += (demand.getTTL() - occupationTimeLeft) * segment.getRange().getLength();
				}
		} else
			for (SpectrumSegment segment : spectrum1.merge(spectrum2).getSegments())
				if (segment instanceof FreeSpectrumSegment)
					slices += demand.getTTL() * segment.getRange().getLength();
				else if (segment instanceof AllocatableSpectrumSegment) {
					if (segment instanceof BackupSpectrumSegment && ((BackupSpectrumSegment) segment).isDisjoint(demand))
						slices += demand.getTTL() * segment.getRange().getLength();
					else {
						int occupationTimeLeft = ((AllocatableSpectrumSegment) segment).getOccupationTimeLeft();
						if (occupationTimeLeft < demand.getTTL())
							slices += (demand.getTTL() - occupationTimeLeft) * segment.getRange().getLength();
					}
				}
		
		return slices / (double) demand.getTTL() * spectrum2.getSlicesCount();
	}
	
	private double s(Network network, NetworkNode node1, NetworkNode node2, Demand demand) {
		double min = Double.MAX_VALUE;
		
		for (NetworkNode source : network.getAdjacentNodes(node1))
			if (source != node2)
				min = Math.min(s(network.getLinkSlices(source, node1), network.getLinkSlices(node1, node2), demand), min);
		for (NetworkNode destination : network.getAdjacentNodes(node2))
			if (destination != node1)
				min = Math.min(s(network.getLinkSlices(node1, node2), network.getLinkSlices(node2, destination), demand), min);
		
		return min;
	}
	
	private double mnc(final Network network, final PartedPath path, final Demand demand) {
		final Container<Double> min = new Container<>(Double.MAX_VALUE);
		final Container<NetworkNode> source = new Container<>(null);
		
		path.forEachNode((destination) -> {
			if (source.value != null)
				min.value = Math.min(s(network, source.value, destination, demand), min.value);
			
			source.value = destination;
		});
		
		return min.value;
	}
}
