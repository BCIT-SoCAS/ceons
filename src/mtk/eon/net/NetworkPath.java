package mtk.eon.net;

import mtk.eon.graph.Path;

/**
 * Path between nodes
 * @author Michal
 *
 */
public class NetworkPath extends Path<NetworkNode> {

	int length;
	public double[][] costs = new double[6][40];
	public int[][] energy = new int[6][40];
	
	public NetworkPath(NetworkNode[] path, int length) {
		super(path);
		this.length = length;
	}
	
	public int getLength() {
		return length;
	}
	
	public boolean isDisjoint(PartedPath path) {
		for (int i = 0; i < size(); i++) {
			int index = path.path.indexOf(get(i));
			if (index == -1) continue;
			if (i != 0 && index != 0 && get(i - 1).equals(path.path.get(index - 1)) || i != 0 && index != path.path.size() - 1 && get(i - 1).equals(path.path.get(index + 1)) ||
				i != size() - 1 && index != 0 && get(i + 1).equals(path.path.get(index - 1)) || i != size() - 1 && index != path.path.size() - 1 && get(i + 1).equals(path.path.get(index + 1)))
				return false;
		}
		return true;
	}
	
	@Override
	public int compareTo(Path<NetworkNode> o) {
//		NetworkPath other = (NetworkPath) o;
//		if (length < other.length) return -1;
//		else if (length == other.length) return 0;
//		else return 1;
		NetworkPath other = (NetworkPath) o;
		int result = Integer.compare(size(), other.size());
		return result == 0 ? Integer.compare(length, other.length) : result;
	}
}
