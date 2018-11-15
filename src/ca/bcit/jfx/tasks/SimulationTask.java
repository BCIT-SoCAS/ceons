package ca.bcit.jfx.tasks;

import ca.bcit.io.Logger;
import ca.bcit.net.Simulation;
import javafx.concurrent.Task;

public class SimulationTask extends Task<Void> {
	
	private final Simulation simulation;
	private final long seed;
	private final int demandsCount;
	private final int erlang;
	private final double alpha;
	private final boolean replicaPreservation;
	private final String rangeList;
	
	public SimulationTask(Simulation simulation, long seed, double alpha, int erlang, int demandsCount, boolean replicaPreservation, String rangeList) {
		this.simulation = simulation;
		this.seed = seed;
		this.erlang = erlang;
		this.demandsCount = demandsCount;
		this.alpha = alpha;
		this.replicaPreservation = replicaPreservation;
		this.rangeList = rangeList;
	}
	
	@Override
	protected Void call() {
		try {
			Logger.info("Starting simulation!");
			simulation.simulate(seed, demandsCount, alpha, erlang, replicaPreservation, this, rangeList);
			Logger.info("Simulation finished!");
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
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
}
