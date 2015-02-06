package mtk.eon.net;

import java.util.ArrayList;

public class PathPart {
	NetworkNode source;
	NetworkNode destination;
	int length;
	
	ArrayList<Slices> slices = new ArrayList<Slices>();
	int index;
	int slicesCount;
	
	Modulation modulation;
	int metric = Integer.MAX_VALUE;
	
	public PathPart(NetworkNode source, NetworkNode destination, int length, Slices... slices) {
		this.source = source;
		this.destination = destination;
		this.length = length;
		for (Slices s : slices) this.slices.add(s);
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
		slices.addAll(other.slices);
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
		for (Slices s : slices) {
			occupied += s.getOccupiedSlices();
			all += s.getSlicesCount();
		}
		return occupied / all;
	}
	
	public Slices getSlices() {
		Slices result = new Slices(NetworkLink.NUMBER_OF_SLICES);
		for (Slices slices : this.slices) result.merge(slices);
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
