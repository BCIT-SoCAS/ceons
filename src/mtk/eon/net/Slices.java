package mtk.eon.net;

public class Slices {
	final long[] slices;
	int occupiedSlices;
	
	public Slices(int slicesCount) {
		if (slicesCount <= 0) throw new NetworkException("The number of slices has to be larger than 0!");
		if (slicesCount % 64 != 0) throw new NetworkException("The number of slices has to be divisable by 64!");
		slices = new long[slicesCount / 64];
	}
	
	public long[] getSlices() {
		return slices;
	}
	
	public int getSlicesCount() {
		return slices.length * 64;
	}
	
	public void merge(Slices slices) {
		if (slices.getSlicesCount() != getSlicesCount())
			throw new NetworkException("Cannot merge slices of different lengths!");
		for (int i = 0; i < this.slices.length; i++)
			this.slices[i] |= slices.slices[i];
	}
	
	public long getSegment(int offset) {
		int index = offset >> 6;
		long segmentA = index >= slices.length ? -1L : slices[index];
		long segmentB = index + 1 >= slices.length ? -1L : slices[index + 1];
		int mod = (offset & 63);
		segmentA = segmentA << mod;
		segmentB = mod == 0 ? 0 : segmentB >>> (64 - mod);
		return segmentA | segmentB;
	}
	
	public int canAllocate(int volume) {
		if (volume > 64) throw new NetworkException("Cannot allocate volumes larger than 64 slices!");
		long volumeMask = -1L << (64 - volume);
		for (int i = 0; i <= getSlicesCount() - volume; i += 2)
			if ((getSegment(i) & volumeMask) == 0)
				return i;
		return -1;
	}
	
	public void allocate(int offset, int volume) {
		int index = offset >> 6;
		long volumeMask = -1L << (64 - volume);
		slices[index] |= volumeMask >>> offset;
		if (index + 1 < slices.length && (offset & 63) != 0) {
			slices[index + 1] |= volumeMask << (64 - (offset & 63));
		}
		occupiedSlices += volume;
	}
	
	public void deallocate(int offset, int volume) {
		int index = offset >> 6;
		long volumeMask = -1L << (64 - volume);
		slices[index] ^= volumeMask >>> offset;
		if (index + 1 < slices.length && (offset & 63) != 0) {
			slices[index + 1] ^= volumeMask << (64 - (offset & 63));
		}
		occupiedSlices -= volume;
	}
	
	public int getOccupiedSlices() {
		return occupiedSlices;
	}
	
	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < slices.length; i++)
			result += String.format("%64s", Long.toBinaryString(slices[i])).replace(' ', '0') + 
			(i != slices.length - 1 ? "\n" : "");
		return result;
	}
}
