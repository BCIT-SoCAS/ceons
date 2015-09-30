package mtk.eon.test;

import java.util.ArrayList;

import mtk.eon.net.Network;
import mtk.eon.net.PartedPath;
import mtk.eon.net.demand.Demand;

public class DummyDemand extends Demand {

	public static int sid;
	int id;
	
	public DummyDemand(int volume, int ttl) {
		super(false, false, volume, 0.5f, ttl);
		id = sid++;
	}

	@Override
	public String toString() {
		return "{id: " + id + "}";
	}

	@Override
	public ArrayList<PartedPath> getCandidatePaths(boolean backup, Network network) {
		return null;
	}
}
