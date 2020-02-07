package ca.bcit.net.demand;

import ca.bcit.net.PartedPath;

public class DemandAllocationResult {

	public enum Type {
		SUCCESS, NO_REGENERATORS, NO_SPECTRUM
	}
	
	public static final DemandAllocationResult NO_REGENERATORS = new DemandAllocationResult(Type.NO_REGENERATORS);
	public static final DemandAllocationResult NO_SPECTRUM = new DemandAllocationResult(Type.NO_SPECTRUM);
	
	public final Type type;
	public final PartedPath workingPath;
	private final PartedPath backupPath;
	
	private DemandAllocationResult(Type type) {
		this.type = type;
		workingPath = null;
		backupPath = null;
	}
	
	public DemandAllocationResult(Demand demand) {
		this.type = Type.SUCCESS;
		this.workingPath = demand.getWorkingPath();
		this.backupPath = demand.getBackupPath();
	}
}
