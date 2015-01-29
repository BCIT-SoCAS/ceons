package mtk.eon.net;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class PartedPath implements Comparable<PartedPath>, Iterable<PathPart> {
	
	ArrayList<PathPart> parts = new ArrayList<PathPart>();
	NetworkPath path;
	double occupiedRegeneratorsPercentage;
	double metric = -1.0;
	
	public PartedPath(Network network, NetworkPath path, boolean isUp) {
		int allRegenerators = 0;
		for (int i = 1; i < path.size(); i++) {
			NetworkNode source = path.get(isUp ? i - 1 : path.size() - i);
			NetworkNode destination = path.get(isUp ? i : path.size() - i - 1);
			if (i > 1) {
				occupiedRegeneratorsPercentage += source.occupiedRegenerators;
				allRegenerators += source.regeneratorsCount;
			}
			parts.add(new PathPart(source, destination, network.getLink(source, destination).getLength(), 
					network.getLinkSlices(source, destination)));
		}
		if (allRegenerators != 0) occupiedRegeneratorsPercentage /= allRegenerators;
		else occupiedRegeneratorsPercentage = 1; // TODO TO JEST REGENERATOROWA KUPA...
		this.path = path;
	}
	
	public void calculateMetricFromParts() {
		metric = 0.0;
		for (PathPart part : parts) metric += part.metric;
		metric /= parts.size();
	}
	
	public void setMetric(double metric) {
		this.metric = metric;
	}
	
	public double getMetric() {
		return metric;
	}
	
	public void mergeRegeneratorlessParts() {
		for (int i = 1; i < parts.size(); i++)
			if (!parts.get(i).getSource().hasFreeRegenerators()) {
				parts.get(i - 1).merge(parts.get(i));
				parts.remove(i);
				i--;
			}
	}
	
	public void mergeIdenticalModulation(Network network, int volume) {
		for (int i = 1; i < parts.size(); i++)
			if (parts.get(i - 1).getModulation() == parts.get(i).getModulation() && parts.get(i - 1).getLength() +
					parts.get(i).getLength() <= network.getModulationDistance(parts.get(i).getModulation(), volume)) {
				parts.get(i - 1).merge(parts.get(i));
				parts.remove(i);
				i--;
			}
	}
	
	public Modulation getModulationFromLongestPart() {
		PathPart longestPart = parts.get(0), part;
		for (int i = 1; i < parts.size(); i++) {
			part = parts.get(i);
			if (longestPart.getLength() < part.getLength()) longestPart = part;
		}
		return longestPart.getModulation();
	}
	
	public double getOccupiedRegeneratorsPercentage() {
		return occupiedRegeneratorsPercentage;
	}
	
	public int getNeededRegeneratorsCount() {
		return parts.size() - 1;
	}
	
	public boolean allocate(Demand demand) {
		for (PathPart part : parts) {
			Slices slices = part.getSlices();
			int slicesCount = path.slicesConsumption[part.getModulation().ordinal()][(int) Math.ceil(demand.getVolume() / 10) - 1];
			part.index = slices.canAllocate(slicesCount);
			part.slicesCount = slicesCount;
			if (part.index == -1) return false;
		}
		for (PathPart part : parts) {
			if (part != parts.get(0)) part.source.occupyRegenerators(1);
			for	(Slices slices : part.slices) slices.allocate(part.index, part.slicesCount);
		}
		return true;
	}

	public int getPartsCount() {
		return parts.size();
	}
	
	@Override
	public Iterator<PathPart> iterator() {
		return parts.iterator();
	}

	@Override
	public int compareTo(PartedPath other) {
		if (metric < other.metric) return -1;
		else if (metric == other.metric) return 0;
		else return 1;
	}
	
	private static class PartedPathLengthComparator implements Comparator<PartedPath> {

		@Override
		public int compare(PartedPath path1, PartedPath path2) {
			return path1.path.compareTo(path2.path);
		}
	}
	
	public static final PartedPathLengthComparator LENGTH_COMPARATOR = new PartedPathLengthComparator();
}
