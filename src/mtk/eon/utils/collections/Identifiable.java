package mtk.eon.utils.collections;

public abstract class Identifiable {
	
	int id = -1;
	
	public final int getID() {
		return id;
	}
	
	@Override
	public final int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object o) { // TODO temp final removal
		if (o == null) return false;
		if (o.getClass() != this.getClass()) return false;
		return getID() == ((Identifiable) o).getID();
	}
}
