package mtk.eon.net.spectrum;

import mtk.eon.net.demand.Demand;

public class WorkingSpectrumSegment extends AbstractSpectrumSegment {

	Demand demand;
	
	public WorkingSpectrumSegment(int offset, int volume, Demand demand) {
		super(offset, volume);
		this.demand = demand;
	}

	public Demand getOwner() {
		return demand;
	}
	
	@Override
	public Type getType() {
		return Type.WORKING;
	}
	
	@Override
	public boolean canOverlap(SpectrumSegment other) {
		return other.getType() == Type.FREE;
	}

	@Override
	public SpectrumSegment join(SpectrumSegment other) {
		SpectrumSegment result = super.join(other);
		if (!demand.equals(((WorkingSpectrumSegment) other).getOwner())) throw new SpectrumException("Cannot join two Type.WORKING SpectrumSegments which demands are not the same.");
		return result;
	}

	@Override
	public SpectrumSegment merge(SpectrumSegment other) {
		if (other.getType() == Type.MULTI) return other.merge(this);
		if (isOverlapping(other)) {
			if (other.getType() == Type.WORKING) return new MultiSpectrumSegment(subtract(other), other.subtract(this), multiply(other));
			else return mergeWorkignNonWorking(other);
		} else return new MultiSpectrumSegment(this, other);
	}
	
	SpectrumSegment mergeWorkignNonWorking(SpectrumSegment other) {
		SpectrumSegment subtraction = other.subtract(this);
		if (subtraction != null) return new MultiSpectrumSegment(subtraction, this);
		else return this;
	}

	@Override
	public SpectrumSegment multiply(SpectrumSegment other) {
		SpectrumSegment result = super.multiply(other);
		if (other.getType() == Type.WORKING && ((WorkingSpectrumSegment) other).demand.getTTL() > demand.getTTL()) ((WorkingSpectrumSegment) result).demand = ((WorkingSpectrumSegment) other).demand;
		return result;
	}
	
	@Override
	public SpectrumSegment partialClone(int offset, int volume) {
		return new WorkingSpectrumSegment(offset, volume, demand);
	}

	@Override
	public String toString() {
		String result = super.toString();
		return result.substring(0, result.length() - 1) + ", Demand: " + demand + "}";
	}
}
