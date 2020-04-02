package ca.bcit.jfx.tasks;

import ca.bcit.io.Logger;
import ca.bcit.jfx.controllers.SimulationMenuController;
import ca.bcit.net.Simulation;
import ca.bcit.utils.LocaleUtils;
import javafx.concurrent.Task;

public class SimulationTask extends Task<Void> {
	
	private final Simulation simulation;
	private final long seed;
	private final int demandsCount;
	private final int erlang;
	private final int cores;
	private final double alpha;
	private final boolean replicaPreservation;
	private final SimulationMenuController simulationMenuController;

	public SimulationTask(Simulation simulation, long seed, int cores, double alpha, int erlang, int demandsCount, boolean replicaPreservation, SimulationMenuController controller) {
		this.simulation = simulation;
		this.seed = seed;
		this.erlang = erlang;
		this.demandsCount = demandsCount;
		this.cores = cores;
		this.alpha = alpha;
		this.replicaPreservation = replicaPreservation;
		this.simulationMenuController = controller;
	}

	@Override
	protected Void call() {
		try {
			Logger.info("\n");
			Logger.info(LocaleUtils.translate("starting_simulation") + "! " + "\n\t" + LocaleUtils.translate("simulation_parameter_seed") + ": " + seed + "\n\t" + LocaleUtils.translate("simulation_parameter_alpha") + ": " + alpha + "\n\t" + LocaleUtils.translate("simulation_parameter_erlang") + ": " + erlang +
					"\n\t" + LocaleUtils.translate("simulation_parameter_number_of_requests") + ": " + demandsCount + "\n\t" + LocaleUtils.translate("simulation_parameter_replica_preservation") + ": " + replicaPreservation);
			simulationMenuController.setRunning(true);
			simulation.simulate(seed, demandsCount, alpha, erlang, replicaPreservation, this);
			Logger.info(LocaleUtils.translate("simulation_finished") + "!");
			simulationMenuController.setRunning(false);
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
