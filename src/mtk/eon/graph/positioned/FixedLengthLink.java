package mtk.eon.graph.positioned;


public abstract class FixedLengthLink<T extends FixedLengthLink<T>> implements Comparable<T> {

	int length;
	
	public FixedLengthLink(int length) {
		this.length = length;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public int compareTo(T other) {
		if (length < other.length) return -1;
		else if (length == other.length) return 0;
		else return 1;
	}
}
