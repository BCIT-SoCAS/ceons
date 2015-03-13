package mtk.eon.net.spectrum;

public abstract class AbstractSpectrumSegment extends SpectrumSegment {
	
	private int offset, volume;
	
	public AbstractSpectrumSegment(int offset, int volume) {
		this.offset = offset;
		this.volume = volume;
	}
	
	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public int getVolume() {
		return volume;
	}
}
