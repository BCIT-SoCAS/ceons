package mtk.eon.net.demand;

import java.util.ArrayList;

import mtk.eon.net.Network;
import mtk.eon.net.PartedPath;
import mtk.eon.net.PathPart;
import mtk.eon.net.spectrum.Spectrum;

public abstract class Demand {

	int volume;
	int ttl;
	
	PartedPath path;
	
	public Demand(int volume, int ttl) {
		this.volume = volume;
		this.ttl = ttl;
	}
	
	public abstract ArrayList<PartedPath> getCandidatePaths(Network network);
	
	public int getVolume() {
		return volume;
	}
	
	public int getTTL() {
		return ttl;
	}
	
	public void tick() {
		ttl--;
	}
	
	public boolean isDead() {
		return ttl <= 0;
	}
	
	public boolean isDisjoint(Demand other) {
		return true; // TODO Disjointness checking 
	}
	
	public DemandAllocationResult allocate(Network network, PartedPath path) {
		if (path.allocate(network, this)) {
			this.path = path;
			return new DemandAllocationResult(path);
		}
		else return DemandAllocationResult.NO_SPECTRUM;
	}
	
	public void deallocate() {
		boolean isFirst = true;
		for (PathPart part : path) {
			if (!isFirst) part.getSource().occupyRegenerators(-1);
			else isFirst = false;
			for	(Spectrum slices : part.spectra) slices.deallocate(part.segment);
		}
	}
}
