package mtk.graph.positioned;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import mtk.graph.Graph;

public class RigidNodeCluster<N extends PositionedNode> extends NodeCluster<N> {
	
	HashSet<N> cluster = new HashSet<N>();
	
	public RigidNodeCluster(Graph<N, ? extends FixedLengthLink<?>, ?, ?> graph, N node1, N node2, N node3) {
		super(graph);
		
		float a = graph.getLink(node1, node2).getLength();
		float b = graph.getLink(node1, node3).getLength();
		float c = graph.getLink(node2, node3).getLength();
		float x = 0.5f * (a + b * b / a - c * c / a), y = (float) Math.sqrt(b * b - x * x);
		
		node1.setPosition(0, 0); cluster.add(node1);
		node2.setPosition(graph.getLink(node1, node2).getLength(), 0); cluster.add(node2);
		node3.setPosition(x, y); cluster.add(node3);
	}
	
	public boolean add(N node) {
		ArrayList<N> commonAdjacent = graph.getAdjacentNodes(node);
		commonAdjacent.retainAll(cluster);
		
		if (commonAdjacent.size() < 3) return false;
		
		float x1 = commonAdjacent.get(0).getPosition().getX(), y1 = commonAdjacent.get(0).getPosition().getY(), x1sq = x1 * x1, y1sq = y1 * y1;
		float x2 = commonAdjacent.get(1).getPosition().getX(), y2 = commonAdjacent.get(1).getPosition().getY(), x2sq = x2 * x2, y2sq = y2 * y2;
		float x3 = commonAdjacent.get(2).getPosition().getX(), y3 = commonAdjacent.get(2).getPosition().getY(), x3sq = x3 * x3, y3sq = y3 * y3;
		float l1 = graph.getLink(commonAdjacent.get(0), node).getLength(), l1sq = l1 * l1;
		float l2 = graph.getLink(commonAdjacent.get(1), node).getLength(), l2sq = l2 * l2;
		float l3 = graph.getLink(commonAdjacent.get(2), node).getLength(), l3sq = l3 * l3;
		float coef12sq = (x1sq - x2sq + y1sq - y2sq + l2sq - l1sq), ratio12 = (y1 - y2) / (x1 - x2);
		float coef13sq = (x1sq - x3sq + y1sq - y3sq + l3sq - l1sq), ratio13 = (x1 - x3) / (y1 - y3);
		float x = (coef12sq / (2 * (x1 - x2)) - ratio12 * coef13sq / (2 * (y1 - y3))) / (1 - ratio12 * ratio13);
		float y = (coef13sq / (2 * (y1 - y3)) - ratio13 * coef12sq / (2 * (x1 - x2))) / (1 - ratio12 * ratio13);
		
		node.setPosition(x, y);
		cluster.add(node);
		
		return true;
	}
	
	@Override
	public Collection<N> getNodes() {
		return cluster;
	}

	@Override
	public boolean contains(NodeCluster<N> other) {
		return cluster.containsAll(other.getNodes());
	}

	@Override
	public boolean equals(NodeCluster<N> other) {
		if (other.getNodes().size() != cluster.size()) return false;
		return cluster.containsAll(other.getNodes());
	}
}
