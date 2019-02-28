package ca.bcit.net;

import ca.bcit.ApplicationResources;
import ca.bcit.drawing.FigureControl;
import ca.bcit.io.Logger;
import ca.bcit.jfx.controllers.SimulationMenuController;
import ca.bcit.jfx.tasks.SimulationTask;
import ca.bcit.net.demand.AnycastDemand;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;
import ca.bcit.net.demand.generator.TrafficGenerator;
import ca.bcit.jfx.controllers.MainWindowController;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


/**
 * Main simulation class (start point)
 * 
 * @author Michal
 *
 */
public class Simulation {

	private final Network network;
	private final TrafficGenerator generator;
	private FigureControl list;

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
			SimulationTask task) {

		SimulationMenuController.finished = false;
		SimulationMenuController.cancelled = false;
		clearVolumeValues();
		generator.setErlang(erlang);
		generator.setSeed(seed);
		generator.setReplicaPreservation(replicaPreservation);
		network.setSeed(seed);
		Random linkCutter = new Random(seed);
		try {
			int reportCounter = 0;
			for (; generator.getGeneratedDemandsCount() < demandsCount;) {
				Demand demand = generator.next();

				if (linkCutter.nextDouble() < alpha / erlang)
					for (Demand reallocate : network.cutLink())
						if (reallocate.reallocate())
							handleDemand(reallocate);
						else
							linkFailureBlockedVolume += reallocate.getVolume();
				else {
					handleDemand(demand);
					if (demand instanceof AnycastDemand)
						handleDemand(generator.next());
				}

				network.update();

				// create a GUI update per n simulations.
				int n = 1000;

				// if paused, force GUI to immediately update to current.
				if (SimulationMenuController.paused)  reportCounter = n;

				int nodeCount = network.getNodes().size();
				ArrayList tempNodeArr = new ArrayList();
				int[][] tempSliceArr = new int[nodeCount * (nodeCount + 1) / 2][5];

				// GUI updates
				reportCounter++;
				if (reportCounter > n) {
					for (int i = 0; i < nodeCount; i++) {
						tempNodeArr.add(network.getNodes().get(i).getName()); // name of regenerator at node i
						int tempOccRegen = network.getNodes().get(i).getOccupiedRegenerators(); // occupied regenerators available at node i
						int tempFreeRegen = network.getNodes().get(i).getFreeRegenerators(); // free regenerators available at node i
						network.getNodes().get(i).updateFigure();
						tempNodeArr.add(tempOccRegen);
						tempNodeArr.add(tempFreeRegen);
						tempNodeArr.add(tempFreeRegen * 100 / (tempOccRegen + tempFreeRegen));
					}

					for (int i = 0; i < nodeCount*4; i+=4) {
						String tempStr = "Node: " + tempNodeArr.get(i).toString() + ", Occupied Regenerators: " + tempNodeArr.get(i+1).toString() + ", Free Regenerators: " + tempNodeArr.get(i+2).toString() + ", Available Regenerators: " + tempNodeArr.get(i+3).toString() + '%';
						System.out.println(tempStr);
					}
					System.out.println("---------------------------------------------------------------------------");

					int tempCounter = 0;
					for (int i = 0; i < nodeCount -1; i++) {
						for (int j = i + 1; j < nodeCount - 1; j++) {
							try {
								tempSliceArr[tempCounter][0] = i;
								tempSliceArr[tempCounter][1] = j;
								tempSliceArr[tempCounter][2] = network.getLinkSlices(network.getNodes().get(i), network.getNodes().get(j)).getOccupiedSlices() / 2;
								tempSliceArr[tempCounter][3] = network.getLinkSlices(network.getNodes().get(i), network.getNodes().get(j)).getSlicesCount() / 2;
								tempSliceArr[tempCounter][4] = (tempSliceArr[tempCounter][3] - tempSliceArr[tempCounter][2]) * 100 / tempSliceArr[tempCounter][3];
							} catch (Exception e) {
								tempSliceArr[tempCounter][0] = -1;
							}
							tempCounter++;
						}
					}
					for (int i = 0; i < tempCounter; i++) {
						if (tempSliceArr[i][0] == -1) {
							continue;
						}
						String tempStr = "Slice beteween " + network.getNodes().get(tempSliceArr[i][0]).getName() + " and " + network.getNodes().get(tempSliceArr[i][1]).getName() + ", Occupied: " + tempSliceArr[i][2] + ", Total Slices: " + tempSliceArr[i][3] + ", Free Slices: " + tempSliceArr[i][4] + '%';
						System.out.println(tempStr);
					}
					System.out.println("---------------------------------------------------------------------------");

					reportCounter -= n;
				}

				// logic to calculate
				/*
				network.getLinkSlices().getOccupiedSlices();
				network.getLinkSlices().getSlicesCount();
				*/

				// pause button
				Pause();

				// cancel button
				if (SimulationMenuController.cancelled) {
					break;
				}

				task.updateProgress(generator.getGeneratedDemandsCount(), demandsCount);
			} // loop end here
			// force call the update again here
		} catch (NetworkException e) {
			// error in code
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
		SimulationMenuController.finished = true;

		if (SimulationMenuController.cancelled) {
			Logger.info("Simulation cancelled!");
			throw new RuntimeException("Simulation was cancelled by user");
		}

		Logger.info("Blocked Spectrum: " + (spectrumBlockedVolume / totalVolume) * 100 + "%");
		Logger.info("Blocked Regenerators: " + (regeneratorsBlockedVolume / totalVolume) * 100 + "%");
		Logger.info("Blocked Link Failure: " + (linkFailureBlockedVolume / totalVolume) * 100 + "%");
		File dir = new File("results");
		if (!dir.isDirectory())
			dir.mkdir();
		File save = new File(dir, ApplicationResources.getProject().getName().toUpperCase() + "-" + generator.getName()
				+ "-ERLANG" + erlang + "-ALPHA" + alpha + ".txt");
		try {
			PrintWriter out = new PrintWriter(save);
			out.println("Generator: " + generator.getName());
			out.println("Alpha: " + alpha);
			out.println("Demands count: " + demandsCount);
			out.println("Blocked Spectrum: " + (spectrumBlockedVolume / totalVolume) * 100 + "%");
			out.println("Blocked Regenerators: " + (regeneratorsBlockedVolume / totalVolume) * 100 + "%");
			out.println("Blocked Link Failure: " + (linkFailureBlockedVolume / totalVolume) * 100 + "%");
			out.println("Blocked Unhandled: " + (unhandledVolume / totalVolume) * 100 + "%");
			out.println(
					"Blocked All: "
							+ ((spectrumBlockedVolume / totalVolume) + (regeneratorsBlockedVolume / totalVolume)
									+ (linkFailureBlockedVolume / totalVolume) + (unhandledVolume / totalVolume)) * 100
							+ "%");
			out.println("Average regenerators per allocation: " + (regsPerAllocation / allocations));
			out.close();
		} catch (IOException e) {
			Logger.debug(e);
		}
		// for (Modulation modulation : Modulation.values())
		// Logger.info(modulation.toString() + ": " +
		// modulationsUsage[modulation.ordinal()]);
	}

	private void Pause() {
		while (SimulationMenuController.paused) {
			try {
				Thread.sleep(10);
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
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

	private void handleDemand(Demand demand) {
		DemandAllocationResult result = network.allocateDemand(demand);

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
