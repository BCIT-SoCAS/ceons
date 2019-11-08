package ca.bcit.graph.positioned;

import ca.bcit.graph.Graph;

import java.util.Collection;

abstract class NodeCluster<N extends PositionedNode> {
	final Graph<N, ? extends FixedLengthLink<?>, ?, ?> graph;
	
	NodeCluster(Graph<N, ? extends FixedLengthLink<?>, ?, ?> graph) {
		this.graph = graph;
	}
	
	protected abstract Collection<N> getNodes();
	public abstract boolean contains(NodeCluster<N> cluster);
	protected abstract boolean equals(NodeCluster<N> cluster);

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		if (other instanceof NodeCluster) return equals((NodeCluster<N>) other);
		else return false;
	}
}
