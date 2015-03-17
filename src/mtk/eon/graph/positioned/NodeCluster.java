package mtk.eon.graph.positioned;

import java.util.Collection;

import mtk.eon.graph.Graph;

public abstract class NodeCluster<N extends PositionedNode> {
	
	protected Graph<N, ? extends FixedLengthLink<?>, ?, ?> graph;
	
	public NodeCluster(Graph<N, ? extends FixedLengthLink<?>, ?, ?> graph) {
		this.graph = graph;
	}
	
	public abstract Collection<N> getNodes();
	public abstract boolean contains(NodeCluster<N> cluster);
	public abstract boolean equals(NodeCluster<N> cluster);

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		if (other instanceof NodeCluster) return equals((NodeCluster<N>) other);
		else return false;
	}
}
