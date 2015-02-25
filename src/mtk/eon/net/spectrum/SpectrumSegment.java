package mtk.eon.net.spectrum;

public class SpectrumSegment implements Cloneable {
	
	private Spectrum spectrum;
	private int offset, volume;
	boolean isFree = true;
	
	SpectrumSegment(Spectrum spectrum, int offset, int volume) {
		this.spectrum = spectrum;
		this.offset = offset;
		this.volume = volume;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getVolume() {
		return volume;
	}
	
	public boolean isFree() {
		return isFree;
	}

	@Override
	protected SpectrumSegment clone() throws CloneNotSupportedException {
		return new SpectrumSegment(spectrum, offset, volume);
	}
}
