package mtk.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mtk.general.HashArray;
import mtk.general.Identifiable;
import mtk.general.IdentifiableSet;

public class Graph<N extends Identifiable, L extends Comparable<L>, P extends Path<N>, G extends Graph<N, L, P, G>> {
	
	IdentifiableSet<N> nodes;
	HashArray<Relation<N, L, P>> relations;
	protected PathBuilder<N, P, G> pathBuilder;
	
	@SuppressWarnings("unchecked")
	public Graph(PathBuilder<N, P, G> pathBuilder) {
		nodes = new IdentifiableSet<N>();
		relations = new HashArray<Relation<N, L, P>>(1);
		this.pathBuilder = pathBuilder;
		pathBuilder.graph = (G) this;
	}
	
	public boolean contains(N node) {
		return nodes.contains(node);
	}
	
	protected boolean addNode(N node) {
		if (!nodes.add(node)) return false;
		relations.resize(relationsSize());
		for (N n : nodes)
			if (node != n) {
				Relation<N, L, P> relation = new Relation<N, L, P>(n, node);
				relations.add(relation);
			}
		return true;
	}
	
	public Iterable<N> getNodes() {
		return nodes;
	}
	
	public boolean removeNode(N node) {
		if (!contains(node)) return false;
		for (N n : nodes)
			if (n != node)
				relations.remove(Relation.hash(n.hashCode(), node.hashCode()));
		nodes.remove(node);
		relations.rehash();
		relations.resize(relationsSize());
		return true;
	}
	
	public int getNodesCount() {
		return nodes.size();
	}
	
	public L putLink(N nodeA, N nodeB, L link) {
		if (!nodes.contains(nodeA)) addNode(nodeA);
		if (!nodes.contains(nodeB)) addNode(nodeB);
		Relation<N, L, P> relation = relations.get(Relation.hash(nodeA.hashCode(), nodeB.hashCode()));
		L oldLink = relation.link;
		relation.link = link;
		return oldLink;
	}
	
	public boolean containsLink(N nodeA, N nodeB) {
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
	
	public List<P> getPaths(N nodeA, N nodeB) {
		Relation<N, L, P> relation = relations.get(Relation.hash(nodeA.hashCode(), nodeB.hashCode()));
		if (relation == null) return null;
		return relation.paths;
	}
	
	public ArrayList<L> getConnectedLinks(N node) {
		ArrayList<L> links = new ArrayList<L>();
		
		for (N n : nodes)
			if (n != node) {
				L link = getLink(n, node);
				if (link != null) links.add(link);
			}
		
		return links;
	}
	
	public ArrayList<N> getAdjacentNodes(N node) {
		ArrayList<N> nodes = new ArrayList<N>();
		
		for (N n : this.nodes)
			if (n != node) {
				L link = getLink(n, node);
				if (link != null) nodes.add(n);
			}
		
		return nodes;
	}
	
	public int relationsSize() { // TODO only for testing
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
		return calculatePaths(progressUpdate, Integer.MAX_VALUE);
	}
	
	public int calculatePaths(Runnable progressUpdate, int maxPathsPerPair) {
		for (Relation<N, L, P> relation : relations) {
			relation.paths.clear();
			currentRelation = relation;
			finishNode = relation.nodeB;
			pathBuilder.init();
			pathBuilder.addNode(relation.nodeA);
			depthFirstSearch(relation.nodeA);
			Collections.sort(relation.paths);
			if (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() < 1024 * 1024 * 100) relation.paths = relation.paths.subList(0, maxPathsPerPair - 1);
			if (maxPathsPerPair > relation.paths.size()) {
				maxPathsPerPair = relation.paths.size();
				for (int i = 0; relations.get(i) != relation; i++) relations.get(i).paths = relation.paths.subList(0, maxPathsPerPair);
			} else relation.paths = relation.paths.subList(0, maxPathsPerPair);
			progressUpdate.run();
		}
		return maxPathsPerPair;
	}
}
