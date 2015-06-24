package mtk.eon.jfx.tasks;

import javafx.concurrent.Task;
import mtk.eon.io.Logger;
import mtk.eon.net.Simulation;

public class SimulationTask extends Task<Void> {
	
	Simulation simulation;
	long seed;
	int demandsCount, erlang;
	double alpha;
	boolean replicaPreservation;
	
	public SimulationTask(Simulation simulation, long seed, double alpha, int erlang, int demandsCount, boolean replicaPreservation) {
		this.simulation = simulation;
		this.seed = seed;
		this.erlang = erlang;
		this.demandsCount = demandsCount;
		this.alpha = alpha;
		this.replicaPreservation = replicaPreservation;
	}
	
	@Override
	protected Void call() throws Exception {
		try {
			Logger.info("Starting simulation!");
			simulation.simulate(seed, demandsCount, alpha, erlang, replicaPreservation, this);
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
