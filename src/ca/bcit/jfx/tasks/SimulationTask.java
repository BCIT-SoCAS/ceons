package ca.bcit.jfx.tasks;

import ca.bcit.io.Logger;
import ca.bcit.net.Simulation;
import javafx.concurrent.Task;

import java.util.ResourceBundle;

public class SimulationTask extends Task<Void> {
	
	private final Simulation simulation;
	private final long seed;
	private final int demandsCount;
	private final int erlang;
	private final double alpha;
	private final boolean replicaPreservation;
	private ResourceBundle resources;
	
	public SimulationTask(Simulation simulation, long seed, double alpha, int erlang, int demandsCount, boolean replicaPreservation, ResourceBundle resources) {
		this.resources = resources;
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
			Logger.info(resources.getString("starting_simulation") + "! " + "\n\t" + resources.getString("simulation_parameter_seed") + ": " + seed + "\n\t" + resources.getString("simulation_parameter_alpha") + ": " + alpha + "\n\t" + resources.getString("simulation_parameter_erlang") + ": " + erlang +
					"\n\t" + resources.getString("simulation_parameter_number_of_requests") + ": " + demandsCount + "\n\t" + resources.getString("simulation_parameter_replica_preservation") + ": " + replicaPreservation);
			simulation.simulate(seed, demandsCount, alpha, erlang, replicaPreservation, this);
			Logger.info(resources.getString("simulation_finished") + "!");
		}
		catch (Throwable e) {
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
}
