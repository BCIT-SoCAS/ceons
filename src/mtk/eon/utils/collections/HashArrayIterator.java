package mtk.eon.utils.collections;

import java.util.Iterator;

public class HashArrayIterator<E> implements Iterator<E> {

	HashArray<E> hashArray;
	int hashCode = 0;
	
	public HashArrayIterator(HashArray<E> hashArray) {
		this.hashArray = hashArray;
	}
	
	@Override
	public boolean hasNext() {
		for (; hashCode < hashArray.capacity(); hashCode++) if (hashArray.contains(hashCode)) return true;
		return false;
	}

	@Override
	public E next() {
		if (hasNext()) return hashArray.get(hashCode++);
		else return null;
	}	
}
