package mtk.graph.positioned;

import java.util.Collection;

import mtk.graph.Graph;

public abstract class NodeCluster<T extends PositionedNode> {
	
	Graph<T,? extends FixedLengthLink<?>, ?, ?> graph;
	
	public abstract Collection<T> getNodes();
	public abstract boolean contains(NodeCluster<T> cluster);
	public abstract boolean equals(NodeCluster<T> cluster);

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		if (other instanceof NodeCluster) return equals((NodeCluster<T>) other);
		else return false;
	}
}
