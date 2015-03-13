package mtk.eon.net.spectrum;

import java.util.HashSet;
import java.util.Set;

import mtk.eon.net.demand.Demand;

public class BackupSpectrumSegment extends AbstractSpectrumSegment {
	
	private Set<Demand> demands;
	
	public BackupSpectrumSegment(int offset, int volume, Demand demand) {
		super(offset, volume);
		demands = new HashSet<Demand>();
		demands.add(demand);
	}

	public BackupSpectrumSegment(int offset, int volume, Set<Demand> demands) {
		super(offset, volume);
		this.demands = demands;
	}

	public Set<Demand> getDemands() {
		return demands;
	}
	
	@Override
	public Type getType() {
		return Type.BACKUP;
	}

	@Override
	public boolean canOverlap(SpectrumSegment other) {
//		if (other.getType() == Type.FREE) return true;
//		else if (other.getType() == Type.BACKUP) {
//			for (Demand demand : demands) if (!demand.isDisjoint(other)) return false;
//		}
		return false; // TODO
	}

	@Override
	public SpectrumSegment join(SpectrumSegment other) {
		if (!(other instanceof BackupSpectrumSegment)) throw new SpectrumException("Cannot join Type." + getType() + " SpectrumSegment with Type." + other.getType() + " SpectrumSegment.");
		Set<Demand> othersDemands = ((BackupSpectrumSegment) other).getDemands();
		if (demands.size() != othersDemands.size() || !demands.containsAll(othersDemands)) throw new SpectrumException("Cannot join two Type.BACKUP SpectrumSegments which demand set is not the same.");
		return super.join(other);
	}

	@Override
	public SpectrumSegment merge(SpectrumSegment other) {
		if (other.getType() == Type.MULTI) return other.merge(this);
		if (isOverlapping(other)) {
			switch (other.getType()) {
			case BACKUP: return new MultiSpectrumSegment(subtract(other), other.subtract(this), multiply(other));
			case FREE: return ((FreeSpectrumSegment) other).mergeFreeNonFree(this);
			case WORKING: return ((WorkingSpectrumSegment) other).mergeWorkignNonWorking(this);
			}
			throw new SpectrumException("Great job! You encountered an impossible to encounter exception.");
		} else return new MultiSpectrumSegment(this, other);
	}
	
	@Override
	public SpectrumSegment multiply(SpectrumSegment other) {
		SpectrumSegment result = super.multiply(other);
		if (other.getType() == Type.BACKUP) ((BackupSpectrumSegment) result).demands.addAll(((BackupSpectrumSegment) other).getDemands());
		return result;
	}

	@Override
	public SpectrumSegment partialClone(int offset, int volume) {
		return new BackupSpectrumSegment(offset, volume, new HashSet<Demand>(demands));
	}
	
	@Override
	public String toString() {
		String result = super.toString();
		return result.substring(0, result.length() - 1) + ", Demands: " + demands + "}";
	}
}
