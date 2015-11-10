package mtk.eon.net.demand;

import java.util.ArrayList;

import mtk.eon.net.Network;
import mtk.eon.net.NetworkNode;
import mtk.eon.net.NetworkPath;
import mtk.eon.net.PartedPath;

public class UnicastDemand extends Demand {

	NetworkNode source, destination;
	
	public UnicastDemand(NetworkNode source, NetworkNode destination, boolean reallocate, boolean allocateBackup, int volume, int squeezedVolume, int ttl, int cpu, int memory, int storage) {
		super(reallocate, allocateBackup, volume, squeezedVolume, ttl, cpu, memory, storage);
		this.source = source;
		this.destination = destination;
	}
	
	public UnicastDemand(NetworkNode source, NetworkNode destination, boolean reallocate, boolean allocateBackup, int volume, float squeezeRatio, int ttl, int cpu, int memory, int storage) {
		super(reallocate, allocateBackup, volume, squeezeRatio, ttl, cpu, memory, storage);
		this.source = source;
		this.destination = destination;
	}

	public NetworkNode getSource() {
		return source;
	}
	
	public NetworkNode getDestination() {
		return destination;
	}
	
	@Override
	public ArrayList<PartedPath> getCandidatePaths(boolean backup, Network network) {
		ArrayList<PartedPath> paths = new ArrayList<PartedPath>();
		
		if (backup)
			for (NetworkPath path : network.getPaths(source, destination)) {
				if (!network.isInactive(path))
					if (paths.size() >= network.getBestPathsCount()) break;
					else if (path.isDisjoint(workingPath))
						paths.add(new PartedPath(network, path, source == path.get(0)));
			}
		else
			for (NetworkPath path : network.getPaths(source, destination).subList(0, network.getBestPathsCount()))
				if (!network.isInactive(path))
					paths.add(new PartedPath(network, path, source == path.get(0)));
		
		return paths;
	}
	
	@Override
	public String toString() {
		return "UnicastDemand {source: " + source + ", destination: " + destination + ", volume: " + getVolume() + ", ttl: " + getTTL() + "}";
	}
}
