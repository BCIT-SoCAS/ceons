package mtk.eon.net;

public class DemandAllocationResult {

	public static enum Type {
		SUCCESS, NO_REGENERATORS, NO_SPECTRUM;
	}
	
	public static final DemandAllocationResult NO_REGENERATORS = new DemandAllocationResult(Type.NO_REGENERATORS);
	public static final DemandAllocationResult NO_SPECTRUM = new DemandAllocationResult(Type.NO_SPECTRUM);
	
	public final Type type;
	public final PartedPath path;
	
	DemandAllocationResult(Type type) {
		this.type = type;
		path = null;
	}
	
	public DemandAllocationResult(PartedPath path) {
		this.type = Type.SUCCESS;
		this.path = path;
	}
}
