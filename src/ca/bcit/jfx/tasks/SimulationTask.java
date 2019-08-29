package ca.bcit.jfx.tasks;

import ca.bcit.io.Logger;
import ca.bcit.jfx.components.ResizableCanvas;
import ca.bcit.jfx.controllers.SimulationMenuController;
import ca.bcit.net.Simulation;
import javafx.concurrent.Task;

public class SimulationTask extends Task<Void> {
	
	private final Simulation simulation;
	private final long seed;
	private final int demandsCount;
	private final int erlang;
	private final double alpha;
	private final boolean replicaPreservation;
	
	public SimulationTask(Simulation simulation, long seed, double alpha, int erlang, int demandsCount, boolean replicaPreservation) {
		this.simulation = simulation;
		this.seed = seed;
		this.erlang = erlang;
		this.demandsCount = demandsCount;
		this.alpha = alpha;
		this.replicaPreservation = replicaPreservation;
	}
	@Override
	protected Void call() {
		try {
			Logger.info("\n");
			Logger.info("Starting simulation! " + "\n\tSeed: " + seed + "\n\tAlpha: " + alpha + "\n\tErlang: " + erlang +
					"\n\tDemands Count: " + demandsCount + "\n\tReplica Preservation: " + replicaPreservation);
			simulation.simulate(seed, demandsCount, alpha, erlang, replicaPreservation, this);
			Logger.info("Simulation finished!");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void updateProgress(long done, long todo) {
		super.updateProgress(done, todo);
	}
	
	@Override
	public void updateMessage(String message) {
		super.updateMessage(message);
	}

	//GETTERS AND SETTERS
	public Simulation getSimulation() {
		return simulation;
	}

	public long getSeed() {
		return seed;
	}

	public int getDemandsCount() {
		return demandsCount;
	}

	public int getErlang() {
		return erlang;
	}

	public double getAlpha() {
		return alpha;
	}

	public boolean isReplicaPreservation() {
		return replicaPreservation;
	}

}
