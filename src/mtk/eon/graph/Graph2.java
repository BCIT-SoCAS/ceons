package mtk.eon.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class Graph2<N, L, P extends Path<N>> {
	
	Map<N, Map<N, L>> links = new Hashtable<>();
	
	public boolean containsNode(N node) {
		return links.containsKey(node);
	}
	
	public boolean addNode(N node) {
		if (links.containsKey(node)) return false;
		links.put(node, new Hashtable<>());
		return true;
	}
	
	public Set<N> nodeSet() {
		return Collections.unmodifiableSet(links.keySet());
	}
	
	public boolean removeNode(N node) {
		boolean result = links.remove(node) != null;
		if (result)
			for (N other : links.keySet())
				links.get(other).remove(node);
		return result;
	}
	
	public boolean containsLink(N node, N other) {
		return links.containsKey(node) && links.get(node).containsKey(other);
	}
	
	public L putLink(N node, N other, L link) {
		if (link == null) throw new GraphException("Cannot add null link to the graph.");
		addNode(node);
		addNode(other);
		links.get(node).put(other, link);
		return links.get(other).put(node, link);
	}
	
	public L getLink(N node, N other) {
		return links.containsKey(node) ? links.get(node).get(other) : null;
	}
	
	public Collection<L> getConnectedLinks(N node) {
		return links.get(node).values();
	}
	
	public Set<N> getConnectedNodes(N node) {
		return links.get(node).keySet();
	}
	
	public L removeLink(N node, N other) {
		links.get(other).remove(node);
		return links.get(node).remove(other);
	}
	
	public void clear() {
		links.clear();
	}
	
	public int size() {
		return links.size();
	}
}
