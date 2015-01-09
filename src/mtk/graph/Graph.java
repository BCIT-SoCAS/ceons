package mtk.graph;

import java.util.ArrayList;
import java.util.Iterator;

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
	
	void addNode(N node) {
		nodes.add(node);
		relations.resize(relationsSize());
		for (N n : nodes)
			if (node != n) {
				Relation<N, L, P> relation = new Relation<N, L, P>(n, node);
				relations.add(relation);
			}
	}
	
	public Iterator<N> getNodesIterator() {
		return nodes.iterator();
	}
	
	public void removeNode(N node) {
		for (N n : nodes)
			if (n != node)
				relations.remove(Relation.hash(n.hashCode(), node.hashCode()));
		nodes.remove(node);
		relations.rehash();
		relations.resize(relationsSize());
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
		return relations.get(Relation.hash(nodeA.hashCode(), nodeB.hashCode())).link != null;
	}
	
	public L getLink(N nodeA, N nodeB) {
		return relations.get(Relation.hash(nodeA.hashCode(), nodeB.hashCode())).link;
	}
	
	public L removeLink(N nodeA, N nodeB) {
		Relation<N, L, P> relation = relations.get(Relation.hash(nodeA.hashCode(), nodeB.hashCode()));
		L oldLink = relation.link;
		relation.link = null;
		return oldLink;
	}
	
	/**
	 * 
	 * @deprecated This method stinks and should not be there...
	 */
	@Deprecated
	public N getNodeByID(int id) {
		if (!nodes.contains(id)) return null;
		return nodes.get(id);
	}
	
	public ArrayList<P> getPaths(N nodeA, N nodeB) {
		return relations.get(Relation.hash(nodeA.hashCode(), nodeB.hashCode())).paths;
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
	
	int relationsSize() {
		return nodes.size() * (nodes.size() - 1) / 2;
	}
}
