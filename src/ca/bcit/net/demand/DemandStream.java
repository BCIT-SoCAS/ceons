package ca.bcit.net.demand;

public interface DemandStream<D extends Demand> {
	D next();
	int getGeneratedDemandsCount();
}
