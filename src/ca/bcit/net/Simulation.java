package ca.bcit.net;

import ca.bcit.ApplicationResources;
import ca.bcit.io.Logger;
import ca.bcit.jfx.components.ResizableCanvas;
import ca.bcit.jfx.controllers.SimulationMenuController;
import ca.bcit.jfx.tasks.SimulationTask;
import ca.bcit.net.demand.AnycastDemand;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;
import ca.bcit.net.demand.generator.TrafficGenerator;

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
	private Network network;
	private TrafficGenerator generator;
	private double totalVolume;
	private double spectrumBlockedVolume;
	private double regeneratorsBlockedVolume;
	private double linkFailureBlockedVolume;
	private double regsPerAllocation;
	private double allocations;
	private double unhandledVolume;
	private final double[] modulationsUsage = new double[6];
	private boolean runAgain;

	public Simulation(){

	}

	public Simulation(Network network, TrafficGenerator generator) {
		this.network = network;
		this.generator = generator;
	}

	public void simulate(long seed, int demandsCount, double alpha, int erlang, boolean replicaPreservation,
			SimulationTask task) {
		SimulationMenuController.finished = false;
		SimulationMenuController.cancelled = false;
		clearVolumeValues();

		//For development set to debug, for release set to info
		Logger.setLoggerLevel(Logger.LoggerLevel.DEBUG);
		generator.setErlang(erlang);
		generator.setSeed(seed);
		generator.setReplicaPreservation(replicaPreservation);
		network.setSeed(seed);
		Random linkCutter = new Random(seed);

		try {
			ResizableCanvas.getParentController().updateGraph();
			int reportCounter = 0;
			for (; generator.getGeneratedDemandsCount() < demandsCount;) {
				SimulationMenuController.started = true;

				Demand demand = generator.next();

				// handle the demand for the specific simulation
				if (linkCutter.nextDouble() < alpha / erlang)
					for (Demand reallocate : network.cutLink())
						if (reallocate.reallocate())
							handleDemand(reallocate);
						else {
							linkFailureBlockedVolume += reallocate.getVolume();
							ResizableCanvas.getParentController().linkFailureBlockedVolume += reallocate.getVolume();
						}
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

				reportCounter++;
				if (reportCounter > n) {

					for (int i = 0; i < nodeCount; i++) {
						tempNodeArr.add(network.getNodes().get(i).getName()); // name of regenerator at node i
						int tempOccRegen = network.getNodes().get(i).getOccupiedRegenerators(); // occupied regenerators available at node i
						int tempFreeRegen = network.getNodes().get(i).getFreeRegenerators(); // free regenerators available at node i
						//network.getNodes().get(i).updateFigure();
						tempNodeArr.add(tempOccRegen);
						tempNodeArr.add(tempFreeRegen);
						tempNodeArr.add(tempFreeRegen * 100 / (tempOccRegen + tempFreeRegen));
					}

//					int tempCounter = 0;
//					for (int i = 0; i < nodeCount -1; i++) {
//						for (int j = i + 1; j < nodeCount - 1; j++) {
//							try {
//								tempSliceArr[tempCounter][0] = i;
//								tempSliceArr[tempCounter][1] = j;
//								tempSliceArr[tempCounter][2] = network.getLinkSlices(network.getNodes().get(i), network.getNodes().get(j)).getOccupiedSlices() / 2;
//								tempSliceArr[tempCounter][3] = network.getLinkSlices(network.getNodes().get(i), network.getNodes().get(j)).getSlicesCount() / 2;
//								tempSliceArr[tempCounter][4] = (tempSliceArr[tempCounter][3] - tempSliceArr[tempCounter][2]) * 100 / tempSliceArr[tempCounter][3];
//							} catch (Exception e) {
//								tempSliceArr[tempCounter][0] = -1;
//							}
//							tempCounter++;
//						}
//					}
					reportCounter -= n;
				}

				// pause button
				pause();

				// cancel button
				if (SimulationMenuController.cancelled) {
					break;
				}

				task.updateProgress(generator.getGeneratedDemandsCount(), demandsCount);
			} // loop end here
			ResizableCanvas.getParentController().stopUpdateGraph();
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
			ResizableCanvas.getParentController().totalVolume += unhandledVolume;
		}

		// wait for internal cleanup after simulation is done
//		network.waitForDemandsDeath();

		// signal GUI menus that simulation is complete
		SimulationMenuController.finished = true;
		ResizableCanvas.getParentController().updateGraph();

		// throw error to avoid printing out data report for cancelled simulations
		if (SimulationMenuController.cancelled) {
			Logger.info("Simulation cancelled!");
		}

		// print basic data in the internal console
		Logger.info("Blocked Spectrum: " + (spectrumBlockedVolume / totalVolume) * 100 + "%");
		Logger.info("Blocked Regenerators: " + (regeneratorsBlockedVolume / totalVolume) * 100 + "%");
		Logger.info("Blocked Link Failure: " + (linkFailureBlockedVolume / totalVolume) * 100 + "%");

		// write the resulting data of a successful simulation to file
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
	}

	/**
	 * For use during an active simulation only. Place the simulation thread to sleep while pause is active.
	 */
	private void pause() {
		while (SimulationMenuController.paused) {
			try {
				Thread.sleep(10);
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Reset parameters to be used in a new simulation. Called before a set of simulations start.
	 */
	private void clearVolumeValues() {
		this.totalVolume = 0;
		this.spectrumBlockedVolume = 0;
		this.regeneratorsBlockedVolume = 0;
		this.linkFailureBlockedVolume = 0;
		this.regsPerAllocation = 0;
		this.allocations = 0;
		this.unhandledVolume = 0;
		ResizableCanvas.getParentController().totalVolume = 0;
		ResizableCanvas.getParentController().spectrumBlockedVolume = 0;
		ResizableCanvas.getParentController().regeneratorsBlockedVolume = 0;
		ResizableCanvas.getParentController().linkFailureBlockedVolume = 0;
		for (Map.Entry<String, NetworkNode> entries: network.nodes.entrySet()){
			entries.getValue().clearOccupied();
		}
	}

	/**
	 * Process a specific demand request. If the demand is impossible to fulfill, the cause is recorded.
	 * If the demand can be fulfilled, resources will be consumed.
	 * @param demand the demand in question
	 */
	private void handleDemand(Demand demand) {
		DemandAllocationResult result = network.allocateDemand(demand);

		if (result.workingPath == null)
			switch (result.type) {
			case NO_REGENERATORS:
				regeneratorsBlockedVolume += demand.getVolume();
				ResizableCanvas.getParentController().regeneratorsBlockedVolume += demand.getVolume();
				break;
			case NO_SPECTRUM:
				spectrumBlockedVolume += demand.getVolume();
				ResizableCanvas.getParentController().spectrumBlockedVolume += demand.getVolume();
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
		ResizableCanvas.getParentController().totalVolume += demand.getVolume();
	}
}
