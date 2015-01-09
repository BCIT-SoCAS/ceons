package mtk.eon.net;

import mtk.graph.Path;

public class NetworkPath extends Path<NetworkNode> {

	int length;
	public int[][] slicesConsumption = new int[6][40];
	public double[][] costs = new double[6][40];
	public int[][] energy = new int[6][40];
	
	public NetworkPath(NetworkNode[] path, int length) {
		super(path);
		this.length = length;
	}
	
	public int getLength() {
		return length;
	}
	
	@Override
	public int compareTo(Path<NetworkNode> o) {
		NetworkPath other = (NetworkPath) o;
		if (length < other.length) return -1;
		else if (length == other.length) return 0;
		else return 1;
	}
}
