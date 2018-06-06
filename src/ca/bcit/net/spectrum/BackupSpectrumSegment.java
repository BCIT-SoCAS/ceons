package ca.bcit.net.spectrum;

import ca.bcit.net.demand.Demand;
import ca.bcit.utils.IntegerRange;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BackupSpectrumSegment extends AllocatableSpectrumSegment {
	
	public static final String TYPE = "BACKUP";
	
	private final Set<Demand> demands;
	
	public BackupSpectrumSegment(int offset, int length, Demand demand) {
		super(new IntegerRange(offset, length));
		demands = new HashSet<>();
		demands.add(demand);
	}
	
	public BackupSpectrumSegment(IntegerRange range, Demand demand) {
		super(range);
		demands = new HashSet<>();
		demands.add(demand);
	}

	private BackupSpectrumSegment(IntegerRange range, Set<Demand> demands) {
		super(range);
		this.demands = demands;
	}

	public Set<Demand> getDemands() {
		return Collections.unmodifiableSet(demands);
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean isOwnedBy(Demand demand) {
		return demands.contains(demand);
	}

	@Override
	public int getOccupationTimeLeft() {
		int time = 0;
		for (Demand demand : demands) if (demand.getTTL() > time)
			time = demand.getTTL();
		return time;
	}

	@Override
	public boolean canJoin(SpectrumSegment other) {
		if (getType() != other.getType()) return false;
		return ((BackupSpectrumSegment) other).demands.equals(demands);
	}
	
	public boolean isDisjoint(Demand demand) {
		for (Demand other : demands)
			if (!demand.isDisjoint(other))
				return false; // TODO Compare collections of links instead...
		return true;
	}

	@Override
	public boolean canAllocate(SpectrumSegment other) {
		if (other.getType() == FreeSpectrumSegment.TYPE) return true;
		else if (other.getType() == BackupSpectrumSegment.TYPE) {
			for (Demand demand1 : demands) for (Demand demand2 : ((BackupSpectrumSegment) other).demands) if (!demand1.isDisjoint(demand2)) return false; // TODO Compare collections of links instead...
			return true;
		} else return false;
	}

	@Override
	public BackupSpectrumSegment allocate(IntegerRange range, SpectrumSegment other) {
		if (other.getType() == FreeSpectrumSegment.TYPE) return clone(range);
		else if (other.getType() == BackupSpectrumSegment.TYPE) {
			Set<Demand> demands = new HashSet<>(((BackupSpectrumSegment) other).demands);
			demands.addAll(this.demands);
			return new BackupSpectrumSegment(range, demands);
		} else throw new SpectrumException("BackupSpectrumSegment can only be allocated on FREE or disjoint BACKUP segments");
	}

	@Override
	public SpectrumSegment deallocate(Demand demand) {
		if (!demands.contains(demand)) throw new SpectrumException("Tried do deallocate segment with demand that is not its owner.");
		if (demands.size() == 1) return new FreeSpectrumSegment(range);
		else {
			Set<Demand> demands = new HashSet<>(this.demands);
			demands.remove(demand);
			return new BackupSpectrumSegment(range, demands);
		}
	}

	@Override
	public SpectrumSegment merge(IntegerRange range, SpectrumSegment other) {
		switch(other.getType()) {
		case FreeSpectrumSegment.TYPE: return clone(range);
		case BackupSpectrumSegment.TYPE:
			BackupSpectrumSegment castedOther = (BackupSpectrumSegment) other;
			Set<Demand> demands;
			if (castedOther.demands.size() > this.demands.size()) {
				demands = new HashSet<>(castedOther.demands);
				demands.addAll(this.demands);
			} else {
				demands = new HashSet<>(this.demands);
				demands.addAll(castedOther.demands);
			}
			return new BackupSpectrumSegment(range, demands);
		case WorkingSpectrumSegment.TYPE: return other.clone(range);
		}
		return other.merge(range, this);
	}

	@Override
	public BackupSpectrumSegment clone(IntegerRange range) {
		return new BackupSpectrumSegment(range, demands);
	}
	
	@Override
	public String toString() {
		String result = super.toString();
		return result.substring(0, result.length() - 1) + ", Demands: " + demands + "}";
	}
}
