package ca.bcit.net.spectrum;

import ca.bcit.utils.IntegerRange;

public abstract class SpectrumSegment implements Comparable<SpectrumSegment> {
	final IntegerRange range;
	
	SpectrumSegment(IntegerRange range) {
		this.range = range;
	}
	
	public abstract String getType();
	
	public IntegerRange getRange() {
		return range;
	}
	
	protected abstract boolean canJoin(SpectrumSegment other);
	
	public SpectrumSegment join(SpectrumSegment other) {
		if (!canJoin(other) || !range.isAdjacent(other.range))
			throw new SpectrumException("Segments joining conditions were not fulfilled.");
		return clone(range.add(other.range));
	}
	
	public abstract SpectrumSegment merge(IntegerRange range, SpectrumSegment other);
	
	public abstract SpectrumSegment clone(IntegerRange range);
	
	@Override
	public int compareTo(SpectrumSegment other) {
		return Integer.compare(range.getOffset(), other.range.getOffset());
	}
	
	@Override
	public String toString() {
		return "{Type: " + getType() + ", Offset: " + range.getOffset() + ", Length: " + range.getLength() + "}";
	}
}
