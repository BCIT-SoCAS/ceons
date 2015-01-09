package mtk.eon.net;

public class NetworkLink implements Comparable<NetworkLink> {
	
	public static final int NUMBER_OF_SLICES = 320;
	
	int length;
	final Slices slicesUp = new Slices(NUMBER_OF_SLICES);
	final Slices slicesDown = new Slices(NUMBER_OF_SLICES);
	
	public NetworkLink(int length) {
		this.length = length;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public int compareTo(NetworkLink other) {
		if (length < other.length) return -1;
		else if (length == other.length) return 0;
		else return 1;
	}
}