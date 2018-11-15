package ca.bcit.net;

import ca.bcit.ApplicationResources;
import ca.bcit.io.Logger;
import ca.bcit.jfx.tasks.SimulationTask;
import ca.bcit.net.demand.AnycastDemand;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;
import ca.bcit.net.demand.generator.TrafficGenerator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Random;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;

/**
 * Main simulation class (start point)
 * 
 * @author Michal
 *
 */
public class Simulation {

	private final Network network;
	private final TrafficGenerator generator;

	private double totalVolume;
	private double spectrumBlockedVolume;
	private double regeneratorsBlockedVolume;
	private double linkFailureBlockedVolume;
	private double regsPerAllocation;
	private double allocations;
	private double unhandledVolume;
	private final double[] modulationsUsage = new double[6];

	public Simulation(Network network, TrafficGenerator generator) {
		this.network = network;
		this.generator = generator;
	}

	public void simulate(long seed, int demandsCount, double alpha, int erlang, boolean replicaPreservation,
			SimulationTask task, String rangeList) {
		clearVolumeValues();
		generator.setErlang(erlang);
		generator.setSeed(seed);
		generator.setReplicaPreservation(replicaPreservation);
		network.setSeed(seed);
		Random linkCutter = new Random(seed);
		try {
			for (; generator.getGeneratedDemandsCount() < demandsCount;) {
				Demand demand = generator.next();

				if (linkCutter.nextDouble() < alpha / erlang)
					for (Demand reallocate : network.cutLink())
						if (reallocate.reallocate())
							handleDemand(reallocate, rangeList);
						else
							linkFailureBlockedVolume += reallocate.getVolume();
				else {
					handleDemand(demand, rangeList);
					if (demand instanceof AnycastDemand)
						handleDemand(generator.next(), rangeList);
				}

				network.update();
				task.updateProgress(generator.getGeneratedDemandsCount(), demandsCount);
			}
		} catch (NetworkException e) {
			Logger.info("Network exception: " + e.getMessage());
			for (; generator.getGeneratedDemandsCount() < demandsCount;) {
				Demand demand = generator.next();
				unhandledVolume += demand.getVolume();
				if (demand instanceof AnycastDemand)
					unhandledVolume += generator.next().getVolume();
				task.updateProgress(generator.getGeneratedDemandsCount(), demandsCount);
			}
			totalVolume += unhandledVolume;
		}

		network.waitForDemandsDeath();

		String range_class = "Z";
		Double blockedAll = ((spectrumBlockedVolume / totalVolume) + (regeneratorsBlockedVolume / totalVolume) + (linkFailureBlockedVolume / totalVolume) + (unhandledVolume / totalVolume)) * 100;
		if (blockedAll <= 0.1) {
			range_class = "A";
		} else if (blockedAll <= 1.0) {
			range_class = "B";
		} else if (blockedAll <= 4.0) {
			range_class = "C";
		} else if (blockedAll <= 10.0) {
			range_class = "D";
		} else if (blockedAll <= 20.0){
			range_class = "E";
		} else {
			range_class = "F";
		}

		try {
			Writer output = new BufferedWriter(new FileWriter("results.csv", true));
			output.append(seed+","+demandsCount+","+erlang+","+rangeList+","+range_class+","+blockedAll+"\n");
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Logger.info("Range Class: "+range_class+"  Blocked All: "+blockedAll);
		Logger.info("");
//		Logger.info("Blocked Spectrum: " + (spectrumBlockedVolume / totalVolume) * 100 + "%");
//		Logger.info("Blocked Regenerators: " + (regeneratorsBlockedVolume / totalVolume) * 100 + "%");
//		Logger.info("Blocked Link Failure: " + (linkFailureBlockedVolume / totalVolume) * 100 + "%");
		File dir = new File("results");
		if (!dir.isDirectory())
			dir.mkdir();
		File save = new File(dir, ApplicationResources.getProject().getName().toUpperCase() + "-" + generator.getName()
				+ "-ERLANG" + erlang + "-ALPHA" + alpha + ".txt");


		try {
			PrintWriter out = new PrintWriter(save);
			out.println(demandsCount+","+rangeList+","+range_class+","+blockedAll);
//			out.println("Generator: " + generator.getName());
//			out.println("Alpha: " + alpha);
//			out.println("Demands count: " + demandsCount);
//			out.println("Blocked Spectrum: " + (spectrumBlockedVolume / totalVolume) * 100 + "%");
//			out.println("Blocked Regenerators: " + (regeneratorsBlockedVolume / totalVolume) * 100 + "%");
//			out.println("Blocked Link Failure: " + (linkFailureBlockedVolume / totalVolume) * 100 + "%");
//			out.println("Blocked Unhandled: " + (unhandledVolume / totalVolume) * 100 + "%");
//			out.println(
//					"Blocked All: "
//							+ ((spectrumBlockedVolume / totalVolume) + (regeneratorsBlockedVolume / totalVolume)
//									+ (linkFailureBlockedVolume / totalVolume) + (unhandledVolume / totalVolume)) * 100
//							+ "%");
//			out.println("Average regenerators per allocation: " + (regsPerAllocation / allocations));
			out.close();
		} catch (IOException e) {
			Logger.debug(e);
		}
		// for (Modulation modulation : Modulation.values())
		// Logger.info(modulation.toString() + ": " +
		// modulationsUsage[modulation.ordinal()]);
	}

	private void clearVolumeValues() {
		this.totalVolume = 0;
		this.spectrumBlockedVolume = 0;
		this.regeneratorsBlockedVolume = 0;
		this.linkFailureBlockedVolume = 0;
		this.regsPerAllocation = 0;
		this.allocations = 0;
		this.unhandledVolume = 0;
		for (Map.Entry<String, NetworkNode> entries: network.nodes.entrySet()){
			entries.getValue().clearOccupied();
		}
	}

	private void handleDemand(Demand demand, String rangeList) {
		DemandAllocationResult result = network.allocateDemand(demand, rangeList);

		if (result.workingPath == null)
			switch (result.type) {
			case NO_REGENERATORS:
				regeneratorsBlockedVolume += demand.getVolume();
				break;
			case NO_SPECTRUM:
				spectrumBlockedVolume += demand.getVolume();
				break;
			default:
				break;
			}
		else {
			allocations++;
			regsPerAllocation += demand.getWorkingPath().getPartsCount() - 1;
			if (demand.getBackupPath() != null)
				regsPerAllocation += demand.getBackupPath().getPartsCount() - 1;
			double modulationsUsage[] = new double[6];
			for (PathPart part : result.workingPath)
				modulationsUsage[part.getModulation().ordinal()]++;
			for (int i = 0; i < 6; i++) {
				modulationsUsage[i] /= result.workingPath.getPartsCount();
				this.modulationsUsage[i] += modulationsUsage[i];
			}
		}
		totalVolume += demand.getVolume();
	}
}
