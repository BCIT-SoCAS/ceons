package mtk.eon.net;

import java.util.ArrayList;

public class UnicastDemand extends Demand {

	NetworkNode source, destination;
	
	public UnicastDemand(NetworkNode source, NetworkNode destination, int volume, int ttl) {
		super(volume, ttl);
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
