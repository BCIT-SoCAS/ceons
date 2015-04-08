package mtk.eon.net.demand;


public interface DemandStream<D extends Demand> {
	
	public abstract D next();
	
	public abstract int getGeneratedDemandsCount();
}
