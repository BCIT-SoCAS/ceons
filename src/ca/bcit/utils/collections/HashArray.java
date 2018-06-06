package ca.bcit.utils.collections;

import ca.bcit.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class HashArray<E> implements Collection<E> {

	private Object[] array;
	private int size;
	
	public HashArray(int initialCapacity) {
		if (initialCapacity < 1) throw new HashArrayException("Cannot initialize HashArray with capacity %d", initialCapacity);
		array = new Object[initialCapacity];
	}
	
	@Override
	public boolean isEmpty() {
		return size == 0;
	}
	
	@Override
	public boolean contains(Object element) {
		if (element == null) return false;
		return contains(element.hashCode());
	}
	
	public boolean contains(int hashCode) {
		if (!Utils.checkArrayIndex(array.length, hashCode)) return false;
		else return array[hashCode] != null;
	}
	
	@Override
	public boolean containsAll(Collection<?> collection) {
		for (Object element : collection) if (!contains(element)) return false;
		return true;
	}
	
	@Override
	public boolean add(E element) {
		if (element == null) throw new HashArrayException("Cannot add null element to HashArray!");
		if (!Utils.checkArrayIndex(capacity(), element.hashCode())) throw new HashArrayException("Hash code: '%d' is out of bounds!", element.hashCode());
		if (array[element.hashCode()] == null) size++;
		array[element.hashCode()] = element;
		return true;
	}
	
	@Override
	public boolean addAll(Collection<? extends E> collection) {
		boolean result = false;
		for (E element : collection) result |= add(element);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public E get(int hashCode) {
		if (!Utils.checkArrayIndex(array.length, hashCode)) throw new HashArrayException("Hash code: '%d' is out of bounds!", hashCode);
		return (E) array[hashCode];
	}
	
	public boolean remove(int hashCode) {
		if (!Utils.checkArrayIndex(array.length, hashCode)) throw new HashArrayException("Hash code: '%d' is out of bounds!", hashCode);
		if (array[hashCode] != null) size--;
		array[hashCode] = null;
		return true;
	}
	
	@Override
	public boolean remove(Object element) {
		if (element == null) throw new HashArrayException("Cannot remove null element from HashArray!");
		return remove(element.hashCode());
	}
	
	@Override
	public boolean removeAll(Collection<?> collection) {
		boolean result = false;
		for (Object element : collection) result |= remove(element);
		return result;
	}
	
	@Override
	public boolean retainAll(Collection<?> collection) {
		boolean result = false;
		for (int i = 0; i < capacity(); i++) if (!collection.contains(array[i]) && array[i] != null) result |= remove(array[i]);
		return result;
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
		ArrayList<Object> container = new ArrayList<>();
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
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public void clear() {
		for (int i = 0; i < capacity(); i++) if (array[i] != null) remove(array[i]);
	}
	
	@Override
	public Object[] toArray() {
		int index = 0;
		Object[] result = new Object[size()];
		for (E element : this) {
			result[index] = element;
			index++;
		}
		return result;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] array) {
		if (array.length < size) return (T[]) Arrays.copyOf(toArray(), size, array.getClass());
		System.arraycopy(toArray(), 0, array, 0, size);
		if (array.length > size) array[size] = null; 
		return array;
	}
	
	@Override
	public String toString() {
		return Arrays.asList(array).toString();
	}

	@Override
	public Iterator<E> iterator() {
		return new HashArrayIterator<>(this);
	}
}
