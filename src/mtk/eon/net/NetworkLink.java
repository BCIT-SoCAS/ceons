package mtk.eon.net;

import mtk.eon.net.spectrum.Spectrum;
import mtk.graph.positioned.FixedLengthLink;

public class NetworkLink extends FixedLengthLink<NetworkLink> {
	
	public static final int NUMBER_OF_SLICES = 320;
	
	final Spectrum slicesUp = new Spectrum(NUMBER_OF_SLICES);
	final Spectrum slicesDown = new Spectrum(NUMBER_OF_SLICES);
	
	public NetworkLink(int length) {
		super(length);
	}
}