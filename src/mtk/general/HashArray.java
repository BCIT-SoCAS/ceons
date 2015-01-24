package mtk.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class HashArray<E> implements Iterable<E> {

	Object[] array;
	int size;
	
	public HashArray(int initialCapacity) {
		if (initialCapacity < 1) throw new HashArrayException("Cannot initialize HashArray with capacity %d", initialCapacity);
		array = new Object[initialCapacity];
	}
	
	public boolean contains(E element) {
		if (element == null) return false;
		return contains(element.hashCode());
	}
	
	public boolean contains(int hashCode) {
		if (!Utils.checkArrayIndex(array.length, hashCode)) return false;
		else return array[hashCode] != null;
	}
	
	@SuppressWarnings("unchecked")
	public E add(E element) {
		if (element == null) throw new HashArrayException("Cannot add null element to HashArray!");
		E old = (E) array[element.hashCode()];
		array[element.hashCode()] = element;
		if (old == null) size++;
		return old;
	}
	
	@SuppressWarnings("unchecked")
	public E get(int hashCode) {
		if (!Utils.checkArrayIndex(array.length, hashCode)) throw new HashArrayException("Hash code: '%d' is out of bounds!", hashCode());
		return (E) array[hashCode];
	}
	
	@SuppressWarnings("unchecked")
	public E remove(int hashCode) {
		if (!Utils.checkArrayIndex(array.length, hashCode)) throw new HashArrayException("Hash code: '%d' is out of bounds!", hashCode());
		E old = (E) array[hashCode];
		array[hashCode] = null;
		if (old != null) size--;
		return old;
	}
	
	public E remove(E element) {
		if (element == null) throw new HashArrayException("Cannot remove null element from HashArray!");
		return remove(element.hashCode());
	}
	
	public void resize(int capacity) {
		Object[] array = new Object[capacity];
		size = 0;
		for (int i = 0; i < Math.min(capacity, capacity()); i++) {
			array[i] = this.array[i];
			if (array[i] != null) size++;
		}
		this.array = array;
	}
	
	public void rehash() {
		ArrayList<Object> container = new ArrayList<Object>();
		for (int i = 0; i < capacity(); i++) 
			if (contains(i)) {
				container.add(array[i]);
				array[i] = null;
			}
		for (Object o : container)
			array[o.hashCode()] = o;
	}
	
	public int capacity() {
		return array.length;
	}
	
	public int size() {
		return size;
	}
	
	@Override
	public String toString() {
		return Arrays.asList(array).toString();
	}

	@Override
	public Iterator<E> iterator() {
		return new HashArrayIterator<E>(this);
	}
}
