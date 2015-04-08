package mtk.eon.net.demand;

import java.util.ArrayList;

import mtk.eon.net.Network;
import mtk.eon.net.NetworkNode;
import mtk.eon.net.NetworkPath;
import mtk.eon.net.PartedPath;

public class UnicastDemand extends Demand {

	NetworkNode source, destination;
	
	public UnicastDemand(NetworkNode source, NetworkNode destination, int initVolume, int minVolume, int ttl) {
		super(initVolume, minVolume, ttl);
		this.source = source;
		this.destination = destination;
	}

	@Override
	public ArrayList<PartedPath> getCandidatePaths(Network network) {
		ArrayList<PartedPath> paths = new ArrayList<PartedPath>();
		
		for (NetworkPath path : network.getPaths(source, destination)) paths.add(new PartedPath(network, path, source == path.get(0)));
		while (paths.size() > network.getBestPathsCount()) paths.remove(network.getBestPathsCount());
		
		return paths;
	}

}
