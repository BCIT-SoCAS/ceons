package ca.bcit.net;

import ca.bcit.net.modulation.IModulation;
import ca.bcit.net.spectrum.AllocatableSpectrumSegment;
import ca.bcit.net.spectrum.Spectrum;

import java.util.ArrayList;
import java.util.Collections;

public class PathPart {
	NetworkNode source;
	private NetworkNode destination;
	private int length;
	
	public final ArrayList<Spectrum> spectra = new ArrayList<>();
	AllocatableSpectrumSegment segment;
	
	private IModulation modulation;
	int metric = Integer.MAX_VALUE;
	
	public PathPart(NetworkNode source, NetworkNode destination, int length, Spectrum... spectra) {
		this.source = source;
		this.destination = destination;
		this.length = length;
		Collections.addAll(this.spectra, spectra);
	}
	
	public PathPart merge(PathPart other) {
		if (modulation != other.modulation)
			throw new NetworkException("Cannot merge PathParts with different modulation!");
		if (destination == other.source)
			destination = other.destination;
		else if (source == other.destination)
			source = other.source;
		else
			throw new NetworkException("Cannot merge PathParts that are not adjacent!");
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
		Spectrum result = new Spectrum(Core.NUMBER_OF_SLICES);
		for (Spectrum slices : this.spectra)
			result = result.merge(slices);
		return result;
	}
	
	public void setModulationIfBetter(IModulation modulation, int metric) {
		if (metric < this.metric) {
			this.metric = metric;
			this.modulation = modulation;
		}
	}
	
	public void setModulation(IModulation modulation, int metric) {
		this.metric = metric;
		this.modulation = modulation;
	}
	
	public IModulation getModulation() {
		return modulation;
	}
}
