package mtk.graph;

import mtk.general.Identifiable;

public abstract class PathBuilder<N extends Identifiable, P extends Path<N>, G extends Graph<N, ?, P, G>> {
	
	G graph;
	
	public abstract void init();
	public abstract boolean contains(N node);
	public abstract void addNode(N node);
	public abstract void removeTail();
	public abstract P getPath();
	protected G getGraph() {
		return graph;
	}
}
