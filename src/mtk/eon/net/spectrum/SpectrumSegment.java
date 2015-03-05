package mtk.eon.net.spectrum;

public class SpectrumSegment implements Comparable<SpectrumSegment>, Cloneable {
	
	private Spectrum spectrum;
	private int offset, volume;
	private boolean isFree;
	
	SpectrumSegment(Spectrum spectrum, int offset, int volume, boolean isFree) {
		this.spectrum = spectrum;
		this.offset = offset;
		this.volume = volume;
		this.isFree = isFree;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getEndOffset() {
		return offset + volume;
	}
	
	public int getVolume() {
		return volume;
	}
	
	public boolean isFree() {
		return isFree;
	}
	
	public boolean isAdjacent(SpectrumSegment other) {
		int minOffset = Math.min(offset, other.offset);
		return minOffset == offset && getEndOffset() == other.offset || minOffset == other.offset && other.getEndOffset() == offset;
	}
	
	public SpectrumSegment merge(SpectrumSegment other) {
		if (spectrum != other.spectrum) throw new SpectrumException(SpectrumException.Type.DIFFERENT_SPECTRA_SEGMENTS_MERGE);
		if (isAdjacent(other))
			return new SpectrumSegment(spectrum, Math.min(offset, other.offset), volume + other.volume);
		else throw new SpectrumException(SpectrumException.Type.NOT_ADJACENT_SEGMENTS_MERGE);
	}

	@Override
	protected SpectrumSegment clone() throws CloneNotSupportedException {
		return new SpectrumSegment(spectrum, offset, volume);
	}

	@Override
	public int compareTo(SpectrumSegment other) {
		return Integer.compare(offset, other.offset);
	}
}
