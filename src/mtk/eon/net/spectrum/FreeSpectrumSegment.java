package mtk.eon.net.spectrum;


public class FreeSpectrumSegment extends AbstractSpectrumSegment {
	
	public FreeSpectrumSegment(int offset, int volume) {
		super(offset, volume);
	}

	@Override
	public Type getType() {
		return Type.FREE;
	}

	@Override
	public boolean canOverlap(SpectrumSegment other) {
		return true; // TODO think about it
	}

	@Override
	public SpectrumSegment merge(SpectrumSegment other) {
		if (other.getType() == Type.MULTI) return other.merge(this);
		if (isOverlapping(other)) {
			if (other.getType() == Type.FREE) return add(other);
			else return mergeFreeNonFree(other);
		} else return new MultiSpectrumSegment(this, other);
	}
	
	SpectrumSegment mergeFreeNonFree(SpectrumSegment other) {
		SpectrumSegment subtraction = subtract(other);
		if (subtraction != null) return new MultiSpectrumSegment(subtraction, other);
		else return other;
	}

	@Override
	public SpectrumSegment partialClone(int offset, int volume) {
		return new FreeSpectrumSegment(offset, volume);
	}
}
