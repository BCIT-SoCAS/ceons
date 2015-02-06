package mtk.eon.net;

import java.util.ArrayList;

import mtk.graph.PathBuilder;

public class NetworkPathBuilder extends PathBuilder<NetworkNode, NetworkPath, Network> {
	ArrayList<NetworkNode> path;
	int length;
	
	@Override
	public void init() {
		path = new ArrayList<NetworkNode>();
	}

	@Override
	public boolean contains(NetworkNode node) {
		return path.contains(node);
	}

	@Override
	public void addNode(NetworkNode node) {
		path.add(node);
		if (path.size() > 1)
			length += getGraph().getLink(node, path.get(path.size() - 2)).getLength();
	}

	@Override
	public void removeTail() {
		NetworkNode node = path.remove(path.size() - 1);
		if (path.size() > 0)
			length -= getGraph().getLink(node, path.get(path.size() - 1)).getLength();
	}

	@Override
	public NetworkPath getPath() {
		return new NetworkPath(path.toArray(new NetworkNode[path.size()]), length);
	}
}
