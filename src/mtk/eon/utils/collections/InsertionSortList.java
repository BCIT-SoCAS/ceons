package mtk.eon.utils.collections;

import java.util.ArrayList;
import java.util.Collection;

public class InsertionSortList<E extends Comparable<? super E>> extends ArrayList<E> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5059547032920524156L;
	
	public InsertionSortList() {
		super();
	}
	
	public InsertionSortList(Collection<? extends E> c) {
		super(c);
	}
	
	public InsertionSortList(int initialCapacity) {
		super(initialCapacity);
	}
	
	@Override
	public boolean add(E e) {
		return addSort(e) != -1;
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean result = false;
		for (E e : c) if (addSort(e) >= 0) result = true;
		return result;
	}
	
	public int addSort(E e) {
		if (e == null) return -1;
		for (int i = 0; i < size(); i++) if (e.compareTo(get(i)) < 0) {
			super.add(i, e);
			return i;
		}
		return super.add(e) ? size() : -1;
	}
	
	public boolean trustedAdd(E e) {
		return super.add(e);
	}
	
	public boolean trustedAddAll(Collection<? extends E> c) {
		return super.addAll(c);
	}
	
	public E last() {
		return get(size() - 1);
	}
}
