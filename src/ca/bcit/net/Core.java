package ca.bcit.net;

import ca.bcit.net.spectrum.Spectrum;

public class Core {
    public static final int NUMBER_OF_SLICES = 640;

    public Spectrum slicesUp = new Spectrum(NUMBER_OF_SLICES);
    public Spectrum slicesDown = new Spectrum(NUMBER_OF_SLICES);
}
