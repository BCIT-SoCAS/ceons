package ca.bcit.graph;

import ca.bcit.utils.collections.HashArray;
import ca.bcit.utils.collections.Identifiable;
import ca.bcit.utils.collections.IdentifiableSet;

import java.util.ArrayList;
import java.util.List;

public class Graph<N extends Identifiable, L extends Comparable<L>, P extends Path<N>, G extends Graph<N, L, P, G>> {
	
	private final IdentifiableSet<N> nodes;
	protected final HashArray<Relation<N, L, P>> relations;
	private final PathBuilder<N, P, G> pathBuilder;
	
	@SuppressWarnings("unchecked")
	protected Graph(PathBuilder<N, P, G> pathBuilder) {
		nodes = new IdentifiableSet<>();
		relations = new HashArray<>(1);
		this.pathBuilder = pathBuilder;
		pathBuilder.graph = (G) this;
	}
	
	protected boolean contains(N node) {
		return nodes.contains(node);
	}
	
	protected boolean addNode(N node) {
		if (!nodes.add(node)) return false;
		relations.resize(getNodesPairsCount());
		for (N n : nodes)
			if (node != n) {
				Relation<N, L, P> relation = new Relation<>(n, node);
				relations.add(relation);
			}
		return true;
	}
	
	public List<N> getNodes() {
		return new ArrayList<>(nodes);
	}
	
	public boolean removeNode(N node) {
		if (!contains(node)) return false;
		for (N n : nodes)
			if (n != node)
				relations.remove(Relation.hash(n.hashCode(), node.hashCode()));
		nodes.remove(node);
		relations.rehash();
		relations.resize(getNodesPairsCount());
		return true;
	}
	
	public int getNodesCount() {
		return nodes.size();
	}

	protected L putLink(N nodeA, N nodeB, L link) {
		if (!nodes.contains(nodeA)) addNode(nodeA);
		if (!nodes.contains(nodeB)) addNode(nodeB);
		Relation<N, L, P> relation = relations.get(Relation.hash(nodeA.hashCode(), nodeB.hashCode()));
		L oldLink = relation.link;
		relation.link = link;
		return oldLink;
	}
	
	protected boolean containsLink(N nodeA, N nodeB) {
		if (!contains(nodeA) || !contains(nodeB)) return false;
		return getLink(nodeA, nodeB) != null;
	}
	
	public L getLink(N nodeA, N nodeB) {
		if (nodeA == nodeB) return null;
		Relation<N, L, P> relation = relations.get(Relation.hash(nodeA.hashCode(), nodeB.hashCode()));
		if (relation == null) return null;
		return relation.link;
	}

	public L removeLink(N nodeA, N nodeB) {
		Relation<N, L, P> relation = relations.get(Relation.hash(nodeA.hashCode(), nodeB.hashCode()));
		L oldLink = relation.link;
		relation.link = null;
		if (getConnectedLinks(nodeA).size() == 0) removeNode(nodeA);
		if (getConnectedLinks(nodeB).size() == 0) removeNode(nodeB);
		return oldLink;
	}
	
	@SuppressWarnings("unchecked")
	public List<P> getPaths(N nodeA, N nodeB) {
		Relation<N, L, P> relation = relations.get(Relation.hash(nodeA.hashCode(), nodeB.hashCode()));
		if (relation == null) return null;
		return (List<P>) relation.paths;
	}
	
	private ArrayList<L> getConnectedLinks(N node) {
		ArrayList<L> links = new ArrayList<>();
		
		for (N n : nodes)
			if (n != node) {
				L link = getLink(n, node);
				if (link != null) links.add(link);
			}
		
		return links;
	}
	
	public ArrayList<N> getAdjacentNodes(N node) {
		ArrayList<N> nodes = new ArrayList<>();
		
		for (N n : this.nodes)
			if (n != node) {
				L link = getLink(n, node);
				if (link != null) nodes.add(n);
			}
		
		return nodes;
	}
	
	public List<L> getLinks() {
		List<L> links = new ArrayList<>();
		for (Relation<N, L, P> relation : relations)
			if (relation.hasLink())
				links.add(relation.link);
		return links;
	}

	public int getNodesPairsCount() { // TODO only for testing
		return nodes.size() * (nodes.size() - 1) / 2;
	}
	
	// Pathfinding
	
	private N finishNode;
	private Relation<N, L, P> currentRelation;
	
	private void depthFirstSearch(N currentNode) {
		ArrayList<N> adjacentNodes = getAdjacentNodes(currentNode);
		for (N node : adjacentNodes) {
			if (pathBuilder.contains(node)) continue;
			if (node.equals(finishNode)) {
				pathBuilder.addNode(node);
				currentRelation.paths.add(pathBuilder.getPath());
				pathBuilder.removeTail();
				break;
			}
		}
		for (N node : adjacentNodes) {
			if (pathBuilder.contains(node) || node.equals(finishNode)) continue;
			pathBuilder.addNode(node);
			depthFirstSearch(node);
			pathBuilder.removeTail();
		}
	}
	
	public int calculatePaths(Runnable progressUpdate) {
		int maxPathsPerPair = Integer.MAX_VALUE;
		for (Relation<N, L, P> relation : relations) {
			relation.paths.clear();
			currentRelation = relation;
			finishNode = relation.nodeB;
			pathBuilder.init();
			pathBuilder.addNode(relation.nodeA);
			depthFirstSearch(relation.nodeA);
//			Collections.sort(relation.paths);
			if (relation.paths.size() > maxPathsPerPair) relation.paths.subList(maxPathsPerPair, relation.paths.size()).clear();
			if (Runtime.getRuntime().freeMemory() < 1024 * 1024 * 1000) {
				int max = 0;
				if (maxPathsPerPair == Integer.MAX_VALUE) {
					for (Relation<N, L, P> rel : relations) if (rel.paths.size() > max) max = rel.paths.size();
					maxPathsPerPair = max;
				}
				maxPathsPerPair -= 2;
				for (Relation<N, L, P> rel : relations)
					if (rel.paths.size() > maxPathsPerPair)
						rel.paths.subList(maxPathsPerPair, rel.paths.size()).clear();
			}
			progressUpdate.run();
		}
		return maxPathsPerPair;
	}
}
