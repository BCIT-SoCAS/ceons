package mtk.eon.net;

import mtk.graph.positioned.FixedLengthLink;

public class NetworkLink extends FixedLengthLink<NetworkLink> {
	
	public static final int NUMBER_OF_SLICES = 320;
	
	final Slices slicesUp = new Slices(NUMBER_OF_SLICES);
	final Slices slicesDown = new Slices(NUMBER_OF_SLICES);
	
	public NetworkLink(int length) {
		super(length);
	}
}