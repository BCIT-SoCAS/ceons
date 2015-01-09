package mtk.general;

import java.util.Iterator;

public class HashArrayIterator<E> implements Iterator<E> {

	HashArray<E> hashArray;
	int hashCode = 0;
	
	public HashArrayIterator(HashArray<E> hashArray) {
		this.hashArray = hashArray;
	}
	
	@Override
	public boolean hasNext() {
		while (hashArray.get(hashCode) == null) {
			hashCode++;
			if (hashCode == hashArray.capacity()) return false;
		}
		return true;
	}

	@Override
	public E next() {
		if (hasNext())
			return hashArray.get(hashCode);
		else
			return null;
	}
	
}
