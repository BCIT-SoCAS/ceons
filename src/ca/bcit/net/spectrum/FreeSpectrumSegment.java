package ca.bcit.net.spectrum;

import ca.bcit.utils.IntegerRange;

public class FreeSpectrumSegment extends SpectrumSegment {
	public static final String TYPE = "FREE";
	
	public FreeSpectrumSegment(IntegerRange range) {
		super(range);
	}
	
	public FreeSpectrumSegment(int offset, int length) {
		super(new IntegerRange(offset, length));
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean canJoin(SpectrumSegment other) {
		return other.getType() == FreeSpectrumSegment.TYPE;
	}

	@Override
	public SpectrumSegment merge(IntegerRange range, SpectrumSegment other) {
		return other.clone(range);
	}

	@Override
	public SpectrumSegment clone(IntegerRange range) {
		return new FreeSpectrumSegment(range);
	}
}
