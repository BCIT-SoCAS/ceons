package mtk.eon.net.demand;

import mtk.eon.net.Network;


public abstract class DemandStream<D extends Demand> {
	
	protected final Network network;
	private int generatedDemandsCount;
	private boolean doNetworkTick;
	
	public DemandStream(Network network) {
		this.network = network;
	}
	
	public D next() {
		if (doNetworkTick) {
			network.update();
			doNetworkTick = false;
		}
		return _next();
	}
	
	protected abstract D _next();
	
	protected void doNetworkTick() {
		doNetworkTick = false;
	}
	
	protected int getGeneratedDemandsCount() {
		return generatedDemandsCount;
	}
}
