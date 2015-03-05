package mtk.eon.jfx.tasks;

import javafx.concurrent.Task;
import mtk.eon.ApplicationResources;
import mtk.eon.io.Logger;
import mtk.eon.io.legacy.DemandLoader;

public class SimulationTask extends Task<Void> {
	
	@Override
	protected Void call() throws Exception {
		DemandLoader demandLoader = null;
		try {
			demandLoader = ApplicationResources.getProject().getDefaultDemandLoader(this);
			Logger.info("Starting simulation!");
			demandLoader.loadAndAllocateDemands();
			Logger.info("Simulation finished!");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
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
