package mtk.general;

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
	public final boolean equals(Object o) {
		if (o == null) return false;
		if (o.getClass() != this.getClass()) return false;
		return getID() == ((Identifiable) o).getID();
	}
}
