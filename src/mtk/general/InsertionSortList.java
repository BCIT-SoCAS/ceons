package mtk.general;

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
	
	public int addSort(E e) {
		for (int i = 0; i < size(); i++) if (e.compareTo(get(i)) < 0) {
			super.add(i, e);
			return i;
		}
		return super.add(e) ? size() : -1;
	}
}
