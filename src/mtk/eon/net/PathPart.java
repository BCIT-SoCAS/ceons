package mtk.eon.net;

import java.util.ArrayList;

import mtk.eon.net.spectrum.Spectrum;
import mtk.eon.net.spectrum.SpectrumSegment;

public class PathPart {
	NetworkNode source;
	NetworkNode destination;
	int length;
	
	ArrayList<Spectrum> spectra = new ArrayList<Spectrum>();
	SpectrumSegment segment;
	
	Modulation modulation;
	int metric = Integer.MAX_VALUE;
	
	public PathPart(NetworkNode source, NetworkNode destination, int length, Spectrum... spectra) {
		this.source = source;
		this.destination = destination;
		this.length = length;
		for (Spectrum s : spectra) this.spectra.add(s);
	}
	
	public PathPart merge(PathPart other) {
		if (modulation != other.modulation)
			throw new NetworkException("Cannot merge PathParts with different modulation!");
		if (destination == other.source)
			destination = other.destination;
		else if (source == other.destination)
			source = other.source;
		else throw new NetworkException("Cannot merge PathParts that are not adjacent!");
		length += other.length;
		spectra.addAll(other.spectra);
		return this;
	}
	
	public NetworkNode getSource() {
		return source;
	}
	
	public NetworkNode getDestination() {
		return destination;
	}
	
	public int getLength() {
		return length;
	}
	
	public double getOccupiedSlicesPercentage() {
		double occupied = 0.0;
		double all = 0.0;
		for (Spectrum s : spectra) {
			occupied += s.getOccupiedSlices();
			all += s.getSlicesCount();
		}
		return occupied / all;
	}
	
	public Spectrum getSlices() {
		Spectrum result = new Spectrum(NetworkLink.NUMBER_OF_SLICES);
		for (Spectrum slices : this.spectra) result.merge(slices);
		return result;
	}
	
	public void setModulationIfBetter(Modulation modulation, int metric) {
		if (metric < this.metric) {
			this.metric = metric;
			this.modulation = modulation;
		}
	}
	
	public void setModulation(Modulation modulation, int metric) {
		this.metric = metric;
		this.modulation = modulation;
	}
	
	public Modulation getModulation() {
		return modulation;
	}
}
