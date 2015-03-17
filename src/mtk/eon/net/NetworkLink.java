package mtk.eon.net;

import mtk.eon.graph.positioned.FixedLengthLink;
import mtk.eon.net.spectrum.Spectrum;

public class NetworkLink extends FixedLengthLink<NetworkLink> {
	
	public static final int NUMBER_OF_SLICES = 320;
	
	final Spectrum slicesUp = new Spectrum(NUMBER_OF_SLICES);
	final Spectrum slicesDown = new Spectrum(NUMBER_OF_SLICES);
	
	public NetworkLink(int length) {
		super(length);
	}
}