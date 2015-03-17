package mtk.eon.graph;

import java.util.ArrayList;
import java.util.List;

import mtk.graph.positioned.FixedLengthLink;
import mtk.graph.positioned.NodeCluster;
import mtk.graph.positioned.PositionedNode;
import mtk.graph.positioned.RigidNodeCluster;

public class GraphUtils {
	
	@SuppressWarnings("unchecked")
	public static <N extends PositionedNode, L extends FixedLengthLink<L>, G extends Graph<N, L, ? extends Path<N>, G>> void findMissingNodePositions(G graph) {
		ArrayList<N> nodes = new ArrayList<N>(graph.getNodes());
		ArrayList<N> unlocatedNodes = new ArrayList<N>(nodes);
		ArrayList<NodeCluster> clusters = new ArrayList<NodeCluster>();
		
		for (N node : nodes) {
			ArrayList<N> adjacentUnlocated = (ArrayList<N>) unlocatedNodes.clone();
			adjacentUnlocated.retainAll(graph.getAdjacentNodes(node));
			for (int i = 0; i < adjacentUnlocated.size(); i++) for (int j = 0; j < i; j++)
				if (graph.containsLink(adjacentUnlocated.get(i), adjacentUnlocated.get(j)))
					clusters.add(new RigidNodeCluster<PositionedNode>(graph, node, node2, node3))
		}
	}
}
