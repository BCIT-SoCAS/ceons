package mtk.graph;



public abstract class Path<N> implements Comparable<Path<N>> {

	final N[] path;
	
	public Path(N[] path) {
		this.path = path;
	}
	
	public N get(int i) {
		return path[i];
	}
	
	public int size() {
		return path.length;
	}
}
