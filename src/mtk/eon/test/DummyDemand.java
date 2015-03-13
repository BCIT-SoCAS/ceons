package mtk.eon.test;

import java.util.ArrayList;

import mtk.eon.net.Network;
import mtk.eon.net.PartedPath;
import mtk.eon.net.demand.Demand;

public class DummyDemand extends Demand {

	public static int sid;
	int id;
	
	public DummyDemand(int ttl) {
		super(0, ttl);
		id = sid++;
	}

	@Override
	public ArrayList<PartedPath> getCandidatePaths(Network network) {
		return null;
	}

	@Override
	public String toString() {
		return "{id: " + id + "}";
	}
}
