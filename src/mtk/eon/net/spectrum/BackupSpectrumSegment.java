package mtk.eon.net.spectrum;

import java.util.HashSet;
import java.util.Set;

import mtk.eon.net.demand.Demand;

public class BackupSpectrumSegment extends SpectrumSegment {
	
	private Set<Demand> demands = new HashSet<Demand>();
	
	public BackupSpectrumSegment(Spectrum spectrum, int offset, int volume, Demand demand) {
		super(spectrum, offset, volume);
		demands.add(demand);
	}
	
	public boolean canOverlap(Demand other) {
		for (Demand demand : demands) if (!demand.isDisjoint(other)) return false;
		return true;
	}
}
