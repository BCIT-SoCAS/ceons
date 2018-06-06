package ca.bcit.utils.collections;

public class IdentifiableSet<E extends Identifiable> extends HashArray<E> {
	
	private int freeID;
	
	public IdentifiableSet() {
		super(8);
		freeID = 0;
	}
	
	public IdentifiableSet(int initialCapacity) {
		super(initialCapacity);
		freeID = 0;
	}

	@Override
	public boolean add(E element) {
		if (element.id != -1) return false;
		if (freeID == capacity()) resize(capacity() + 8);
		element.id = freeID;
		freeID++;
		return super.add(element);
	}

	@Override
	public boolean remove(int id) {
		E old = get(id);
		boolean result = super.remove(id);
		for (int i = id + 1; i < size(); i++) get(i).id--;
		rehash();
		if (capacity() - size() > 8) resize(size() + 8);
		if (old != null) old.id = -1;
		return result;
	}
}
