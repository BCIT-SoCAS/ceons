package ca.bcit.net;

import ca.bcit.graph.positioned.FixedLengthLink;
import ca.bcit.net.spectrum.Spectrum;

import java.util.Map;

/**
 * Network link
 *
 */
public class NetworkLink extends FixedLengthLink<NetworkLink> {
	
	public static final int NUMBER_OF_SLICES = 640;
	
	Spectrum slicesUp = new Spectrum(NUMBER_OF_SLICES);
	Spectrum slicesDown = new Spectrum(NUMBER_OF_SLICES);
	
	public NetworkLink(int length) {
		super(length);
	}
	
	@SuppressWarnings("rawtypes")
	public NetworkLink(Map map) {
		super(map);
	}

}