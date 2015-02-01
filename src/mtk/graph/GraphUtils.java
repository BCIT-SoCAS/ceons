package mtk.graph;

import java.util.ArrayList;
import java.util.List;

import mtk.graph.positioned.FixedLengthLink;
import mtk.graph.positioned.NodeCluster;
import mtk.graph.positioned.PositionedNode;

public class GraphUtils {
	
	public static <N extends PositionedNode, L extends FixedLengthLink<L>, G extends Graph<N, L, ? extends Path<N>, G>> void findMissingNodePositions(G graph) {
		List<N> nodes = new ArrayList<N>();
		List<NodeCluster> clusters = new ArrayList<NodeCluster>();
		
		for (N node : nodes) {
			
		}
	}
}
