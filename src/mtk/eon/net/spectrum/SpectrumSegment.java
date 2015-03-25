package mtk.eon.net.spectrum;

import mtk.eon.utils.IntegerRange;


public abstract class SpectrumSegment implements Comparable<SpectrumSegment> {
	
	protected final IntegerRange range;
	
	public SpectrumSegment(IntegerRange range) {
		this.range = range;
	}
	
	public abstract String getType();
	
	public IntegerRange getRange() {
		return range;
	}
	
	public abstract boolean canJoin(SpectrumSegment other);
	
	public SpectrumSegment join(SpectrumSegment other) {
		if (!canJoin(other)) throw new SpectrumException("Segments joining conditions were not fulfilled.");
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
