package ca.bcit.net;

import ca.bcit.ApplicationResources;
import ca.bcit.io.Logger;
import ca.bcit.io.MapLoadingException;
import ca.bcit.io.project.Project;
import ca.bcit.io.project.ProjectFileFormat;
import ca.bcit.jfx.components.ResizableCanvas;
import ca.bcit.jfx.controllers.MainWindowController;
import ca.bcit.jfx.controllers.SimulationMenuController;
import ca.bcit.jfx.tasks.SimulationTask;
import ca.bcit.net.demand.AnycastDemand;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;
import ca.bcit.net.demand.generator.TrafficGenerator;
import ca.bcit.net.spectrum.Spectrum;

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

	public Simulation(Network network, TrafficGenerator generator) {
		this.network = network;
		this.generator = generator;
	}

	public void simulate(long seed, int demandsCount, double alpha, int erlang, boolean replicaPreservation, SimulationTask task) {
		SimulationMenuController.finished = false;
		SimulationMenuController.cancelled = false;
		clearVolumeValues();

		//For development set to debug, for release set to info
		Logger.setLoggerLevel(Logger.LoggerLevel.INFO);
		generator.setErlang(erlang);
		generator.setSeed(seed);
		generator.setReplicaPreservation(replicaPreservation);
		network.setSeed(seed);
		Random linkCutter = new Random(seed);

		try {
			ResizableCanvas.getParentController().updateGraph();

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

				// pause button
				pause();

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
			ResizableCanvas.getParentController().totalVolume += unhandledVolume;
		}

		 //wait for internal cleanup after simulation is done
		network.waitForDemandsDeath();
		ResizableCanvas.getParentController().resetGraph();

		// signal GUI menus that simulation is complete
		SimulationMenuController.finished = true;

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
				+ "-ERLANG" + erlang + "-SEED" + seed + "-ALPHA" + alpha + ".txt");
		try {
			PrintWriter out = new PrintWriter(save);
			out.println("Generator: " + generator.getName());
			out.println("Erlang: " + erlang);
			out.println("Seed: " + seed);
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
		MainWindowController mainWindowController = ResizableCanvas.getParentController();
		Project project = ApplicationResources.getProject();
		Network network = project.getNetwork();
		this.totalVolume = 0;
		this.spectrumBlockedVolume = 0;
		this.regeneratorsBlockedVolume = 0;
		this.linkFailureBlockedVolume = 0;
		this.regsPerAllocation = 0;
		this.allocations = 0;
		this.unhandledVolume = 0;
		mainWindowController.totalVolume = 0;
		mainWindowController.spectrumBlockedVolume = 0;
		mainWindowController.regeneratorsBlockedVolume = 0;
		mainWindowController.linkFailureBlockedVolume = 0;
		for(NetworkNode n : network.getNodes()){
			n.clearOccupied();
			for(NetworkNode n2 : network.getNodes()){
				if(network.containsLink(n, n2)){
					NetworkLink networkLink = network.getLink(n, n2);
					Spectrum spectrum = network.getLinkSlices(n, n2);
					networkLink.slicesUp = new Spectrum(NetworkLink.NUMBER_OF_SLICES);
					networkLink.slicesDown = new Spectrum(NetworkLink.NUMBER_OF_SLICES);
					System.out.println("Network Link: " + networkLink.toString());
					System.out.println("Length: " + networkLink.length);
					System.out.println("Slices Down: " + networkLink.slicesDown);
					System.out.println("Slices Up: " + networkLink.slicesUp);
					System.out.println("Slices Count: " + spectrum.getSlicesCount());
					System.out.println("Occupied Slices: " + spectrum.getOccupiedSlices());
					System.out.println("Segments: " + spectrum.getSegments());
				}
			}
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
