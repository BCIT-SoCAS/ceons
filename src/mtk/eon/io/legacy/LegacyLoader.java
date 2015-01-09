package mtk.eon.io.legacy;

import java.util.ArrayList;
import java.util.HashMap;

import mtk.eon.net.NetworkLink;
import mtk.eon.net.Network;
import mtk.eon.net.NetworkPath;
import mtk.eon.net.NetworkNode;

public class LegacyLoader {
	
	static class Pair {
		NetworkNode a, b;
		
		Pair(NetworkNode a, NetworkNode b) {
			this.a = a;
			this.b = b;
		}
	}

	Network network = new Network();
	HashMap<Integer, NetworkNode> nodes = new HashMap<Integer, NetworkNode>();
	int freeLinkID;
	HashMap<Integer, Pair> links = new HashMap<Integer, Pair>();
	public int candidatePathsCount;
	
	public void initNodes(int nodesCount) {
		for (int i = 0; i < nodesCount; i++) {
			NetworkNode node = new NetworkNode("Node_" + i);
			nodes.put(i, node);
		}
	}
	
	public int getNodesCount() {
		return nodes.size();
	}
	
	public void setRegeneratorsCount(int id, int regeneratorsCount) {
		nodes.get(id).setRegeneratorsCount(regeneratorsCount);
	}
	
	public void addReplica(int id) {
		network.addReplica(nodes.get(id));
	}
	
	public void addLink(int source, int destination, int distance) {
		NetworkLink link = network.getLink(nodes.get(source), nodes.get(destination));
		
		if (link == null) {
			link = new NetworkLink(distance);
			network.putLink(nodes.get(source), nodes.get(destination), link);
		}
		
		links.put(freeLinkID, new Pair(nodes.get(source), nodes.get(destination)));
		freeLinkID++;
	}
	
	public Pair getLink(int id) {
		return links.get(id);
	}
	
	public int getLinksCount() {
		return links.size();
	}
	
	public void addPath(int source, int destination, ArrayList<Pair> links) {
		ArrayList<NetworkNode> path = new ArrayList<NetworkNode>();
		NetworkNode node = nodes.get(source);
		int length = 0;
		outer: while (!links.isEmpty())
			for (int j = 0; j < links.size(); j++) {
				NetworkNode other = null;
				if (links.get(j).a == node) other = links.get(j).b;
				else if (links.get(j).b == node) other = links.get(j).a;
				if (other != null) {
					path.add(node);
					length += network.getLink(node, other).getLength();
					node = other;
					continue outer;
				}
			}
		path.add(node);
		network.getPaths(nodes.get(source), nodes.get(destination)).add(new NetworkPath(path.toArray(
				new NetworkNode[path.size()]), length));
	}
	
	public ArrayList<NetworkPath> getPaths(int source, int destination) {
		return network.getPaths(nodes.get(source), nodes.get(destination));
	}
	
	public Network getNetwork() {
		return network;
	}
}
