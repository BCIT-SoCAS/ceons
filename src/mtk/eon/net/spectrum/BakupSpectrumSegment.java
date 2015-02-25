package mtk.eon.net.spectrum;

import mtk.eon.net.demand.Demand;

public class BakupSpectrumSegment extends SpectrumSegment {
	
	private Demand demand;
	
	public BakupSpectrumSegment(Spectrum spectrum, int offset, int volume, Demand demand) {
		super(spectrum, offset, volume);
		this.demand = demand;
	}
}
