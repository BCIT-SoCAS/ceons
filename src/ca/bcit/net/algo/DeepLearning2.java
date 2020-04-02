package ca.bcit.net.algo;

import ca.bcit.net.*;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;
import ca.bcit.net.demand.generator.TrafficGenerator;
import ca.bcit.net.modulation.IModulation;
import ca.bcit.net.spectrum.NoSpectrumAvailableException;

import java.util.ArrayList;
import java.util.List;

public class DeepLearning2 extends BaseRMSAAlgorithm implements IRMSAAlgorithm{

	public String getKey(){
		return "DLTotalScorePerPath";
	};

	public String getName(){
		return "DLTotalScorePerPath";
	};

	public String getDocumentationURL(){
		return "";
	};

	@Override
	public DemandAllocationResult allocateDemand(Demand demand, Network network) throws InstantiationException, ClassNotFoundException, IllegalAccessException {

		try {
			int volume = (int) Math.ceil(demand.getVolume() / 10.0) - 1;
			List<PartedPath> paths = demand.getCandidatePaths(false, network);
			applyMetricsToCandidatePaths(network, volume, paths);

			int numCandidatePaths = network.getBestPathsCount();

			TrafficGenerator generator = network.getTrafficGenerator();

			Demand demand1 = generator.next();
			Demand demand2 = generator.next();
			Demand demand3 = generator.next();

			List<Double> scores = new ArrayList<>();
			int pathNumber = 0;

			for (PartedPath path : paths) {
				TemporaryDemandResult result = allocateTemporaryDemand(path, demand, network, 0.0);
				for (PartedPath path1 : result.paths) {
					TemporaryDemandResult result1 = allocateTemporaryDemand(path1, demand1, result.network, result.score);
					for (PartedPath path2 : result1.paths) {
						TemporaryDemandResult result2 = allocateTemporaryDemand(path2, demand2, result1.network, result1.score);
						for (PartedPath path3 : result2.paths) {
							TemporaryDemandResult result3 = allocateTemporaryDemand(path3, demand3, result2.network, result2.score);
							scores.add(result3.score);
						}
					}
				}
				double totalScore = 0.0;
				for (int i = (int) (pathNumber * Math.pow(numCandidatePaths, 3)); i < pathNumber * Math.pow(numCandidatePaths, 3) + Math.pow(numCandidatePaths, 3); i++)
					totalScore += scores.get((int) (pathNumber * Math.pow(numCandidatePaths, 3)));

				path.setMetric(totalScore);
				pathNumber += 1;
			}
			sortCandidatePaths(paths);

			allocateWorkingPath(demand, paths);

			if (shouldAllocateBackupPath(demand, paths)) {
				allocateBackupPath(demand, paths);
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

	protected TemporaryDemandResult allocateTemporaryDemand(PartedPath path, Demand demand, Network network, Double score) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		Network tempNetwork = network;
		Demand tempDemand = demand;
		tempNetwork.setDemandAllocationAlgorithm((IRMSAAlgorithm) Class.forName("ca.bcit.net.algo.AMRA").newInstance());
		tempDemand.allocate(path);
		DemandAllocationResult result = tempNetwork.allocateDemand(tempDemand);

		List<PartedPath> paths = tempDemand.getCandidatePaths(false, tempNetwork);
		int tempVolume = (int) Math.ceil(tempDemand.getVolume() / 10.0) - 1;
		applyMetricsToCandidatePaths(tempNetwork, tempVolume, paths);

		if (result.type != DemandAllocationResult.Type.SUCCESS) {
			score += 100;
			return new TemporaryDemandResult(score, tempNetwork, paths);
		}

		path.mergeRegeneratorlessParts();
		score += path.getOccupiedRegeneratorsPercentage();
		for (PathPart part : path) {
			score += part.getOccupiedSlicesPercentage();
		}

		return new TemporaryDemandResult(score, tempNetwork, paths);
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

		filterCandidatePaths(candidatePaths);

		if (candidatePaths.isEmpty())
			throw new NoRegeneratorsAvailableException("There are no candidate paths to allocate the demand.");

		sortCandidatePaths(candidatePaths);
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
