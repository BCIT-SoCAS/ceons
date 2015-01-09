package mtk.general;

public class IdentifiableSet<E extends Identifiable> extends HashArray<E> {
	
	int freeID;
	
	public IdentifiableSet() {
		super(8);
		freeID = 0;
	}
	
	public IdentifiableSet(int initialCapacity) {
		super(initialCapacity);
		freeID = 0;
	}

	@Override
	public E add(E element) {
		if (freeID == capacity()) resize(capacity() + 8);
		element.id = freeID;
		E old = super.add(element);
		freeID++;
		return old;
	}
	
	@Override
	public E get(int id) {
		return super.get(id);
	}

	@Override
	public E remove(int id) {
		for (int i = id + 1; i < size(); i++)
			get(i).id--;
		E old = super.remove(id);
		rehash();
		if (capacity() - size() > 8) resize(size() + 8);
		if (old != null) old.id = -1;
		return old;
	}
}
