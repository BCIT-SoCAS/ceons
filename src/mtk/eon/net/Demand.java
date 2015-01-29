package mtk.eon.net;

import java.util.ArrayList;

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
	
	public DemandAllocationResult allocate(PartedPath path) {
		if (path.allocate(this)) {
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
			for	(Slices slices : part.slices) slices.deallocate(part.index, part.slicesCount);
		}
	}
}
