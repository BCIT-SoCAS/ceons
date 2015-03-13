package mtk.eon.net.spectrum;


public abstract class SpectrumSegment implements Comparable<SpectrumSegment> {
	
	public enum Type {
		FREE, WORKING, BACKUP, MULTI;
	}
	
	public abstract Type getType();
	
	public abstract int getOffset();
	
	public abstract int getVolume();
	
	public int getEndOffset() {
		return getOffset() + getVolume();
	}
	
	public boolean isAdjacent(SpectrumSegment other) {
		return other.getOffset() == getEndOffset() || other.getEndOffset() == getOffset();
	}
	
	public boolean isOverlapping(SpectrumSegment other) {
		return !(getOffset() >= other.getEndOffset() || getEndOffset() <= other.getOffset());
	}
	
	public abstract boolean canOverlap(SpectrumSegment other);
	
	public SpectrumSegment join(SpectrumSegment other) {
		if (other.getType() != getType()) throw new SpectrumException("Cannot join Type." + getType() + " SpectrumSegment with Type." + other.getType() + " SpectrumSegment.");
		if (!isAdjacent(other)) throw new SpectrumException("Cannot join segments that are not adjacent.");
		return partialClone(Math.min(getOffset(), other.getOffset()), getVolume() + other.getVolume());
	}
	
	public abstract SpectrumSegment merge(SpectrumSegment other);

	public SpectrumSegment add(SpectrumSegment other) {
		int offset = Math.min(getOffset(), other.getOffset());
		return partialClone(offset, Math.max(getEndOffset(), other.getEndOffset()) - offset);
	}
	
	public SpectrumSegment subtract(SpectrumSegment other) {
		if (isOverlapping(other)) {
			int overlapCase = (getOffset() < other.getOffset() ? 1 : 0) + (getEndOffset() > other.getEndOffset() ? 2 : 0);
			switch (overlapCase) {
			case 0: return null;
			case 1: return partialClone(getOffset(), other.getOffset() - getOffset());
			case 2: return partialClone(other.getEndOffset(), getEndOffset() - other.getEndOffset());
			case 3: return new MultiSpectrumSegment(partialClone(getOffset(), other.getOffset() - getOffset()), partialClone(other.getEndOffset(), getEndOffset() - other.getEndOffset()));
			}
			throw new SpectrumException("God left us all...");
		} else return partialClone(getOffset(), getVolume());
	}
	
	public SpectrumSegment multiply(SpectrumSegment other) {
		if (isOverlapping(other)) {
			int offset = Math.max(getOffset(), other.getOffset());
			return partialClone(offset, Math.min(getEndOffset(), other.getEndOffset()) - offset);
		} else return null;
	}
	
	public abstract SpectrumSegment partialClone(int offset, int volume);
	
	@Override
	public int compareTo(SpectrumSegment other) {
		return Integer.compare(getOffset(), other.getOffset());
	}
	
	@Override
	public String toString() {
		return "{Type: " + getType() + ", Offset: " + getOffset() + ", Volume: " + getVolume() + "}";
	}
}
