package mtk.eon.net.spectrum;

import java.util.ArrayList;
import java.util.List;

import mtk.eon.net.NetworkException;

public class Spectrum {
	
	public static final SpectrumSegment CANNOT_ALLOCATE = new SpectrumSegment(null, -1, -1);
	
	private List<SpectrumSegment> free = new ArrayList<SpectrumSegment>();
	private List<SpectrumSegment> all = new ArrayList<SpectrumSegment>();
	int slicesCount, occupiedSlices;
	
	public Spectrum(int slicesCount) {
		if (slicesCount <= 0) throw new NetworkException("The number of slices has to be larger than 0!");
		if (slicesCount % 2 != 0) throw new NetworkException("The number of slices has to be even!");
		this.slicesCount = slicesCount;
		SpectrumSegment initSegment = new SpectrumSegment(this, 0, slicesCount);
		free.add(initSegment);
		all.add(initSegment);
	}
	
	public int getSlicesCount() {
		return slicesCount;
	}
	
	public void merge(Spectrum slices) {
		if (slices.getSlicesCount() != getSlicesCount())
			throw new NetworkException("Cannot merge slices of different lengths!");
		for (int i = 0; i < this.slices.length; i++)
			this.slices[i] |= slices.slices[i];
	}
	
	public boolean canAllocate(int volume) {
		for (SpectrumSegment segment : free) if (segment.getVolume() >= volume)
			return true;
		return false;
	}
	
	public SpectrumSegment allocate(int volume) {
		for (int i = 0; i < free.size(); i++) {
			SpectrumSegment segment = free.get(i);
			if (segment.getVolume() >= volume) {
				SpectrumSegment result = new SpectrumSegment(this, segment.getOffset(), volume);
				int allIndex = all.indexOf(segment);
				if (segment.getVolume() != volume) {
					SpectrumSegment newFree = new SpectrumSegment(this, segment.getOffset() + volume, segment.getVolume() - volume);
					free.set(i, newFree);
					all.set(allIndex, newFree);
					all.add(allIndex, result);
				} else {
					free.remove(i);
					all.set(allIndex, result);
				}
				occupiedSlices += volume;
				return result;
			}
		}
		return CANNOT_ALLOCATE;
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
	
	public void deallocate(SpectrumSegment segment) {
		deallocate(segment.getOffset(), segment.getVolume());
	}
	
	public int getOccupiedSlices() {
		return occupiedSlices;
	}
	
	public List<SpectrumSegment> getFreeSegments() {
		List<SpectrumSegment> freeSegments = new ArrayList<SpectrumSegment>();
		int offset = -1;
		for (int i = 0; i < slices.length * 64; i++)
			if (offset == -1) {
				if ((slices[i / 64] & Long.MIN_VALUE) == 0) offset = i;
			} else
				if ((slices[i / 64] & Long.MIN_VALUE) != 0) {
					freeSegments.add(new SpectrumSegment(this, offset, i - offset));
					offset = -1;
				}
		return freeSegments;
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
