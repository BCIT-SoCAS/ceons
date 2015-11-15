package mtk.eon.graph;

import java.util.Arrays;

public abstract class Path<N> implements Comparable<Path<N>> {

	final N[] path;
	
	public Path(N[] path) {
		this.path = path;
	}
	
	public int indexOf(N node) {
		for (int i = 0; i < path.length; i++)
			if (path[i].equals(node))
				return i;
		return -1;
	}
	
	public N get(int i) {
		return path[i];
	}
	
	public int size() {
		return path.length;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(path);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Path<?> other = (Path<?>) obj;
		if (!Arrays.equals(path, other.path))
			return false;
		return true;
	}
	
	
}
