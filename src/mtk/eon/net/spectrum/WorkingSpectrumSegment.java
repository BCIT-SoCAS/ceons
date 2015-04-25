package mtk.eon.net.spectrum;

import mtk.eon.net.demand.Demand;
import mtk.eon.utils.IntegerRange;

public class WorkingSpectrumSegment extends AllocatableSpectrumSegment {

	public static final String TYPE = "WORKING";
	
	private Demand owner;
	
	public WorkingSpectrumSegment(int offset, int length, Demand demand) {
		super(new IntegerRange(offset, length));
		this.owner = demand;
	}
	
	public WorkingSpectrumSegment(IntegerRange range, Demand demand) {
		super(range);
		this.owner = demand;
	}

	public Demand getOwner() {
		return owner;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean isOwnedBy(Demand demand) {
		return owner.equals(demand);
	}

	@Override
	public int getOccupationTimeLeft() {
		return owner.getTTL();
	}

	@Override
	public boolean canJoin(SpectrumSegment other) {
		if (getType() != other.getType()) return false;
		return ((WorkingSpectrumSegment) other).owner.equals(owner);
	}

	@Override
	public boolean canAllocate(SpectrumSegment other) {
		return other.getType() == FreeSpectrumSegment.TYPE;
	}

	@Override
	public WorkingSpectrumSegment allocate(IntegerRange range, SpectrumSegment other) {
		if (other.getType() != FreeSpectrumSegment.TYPE) throw new SpectrumException("Working spectrum can only by allocated on type-FREE segments.");
		return clone(range);
	}

	@Override
	public SpectrumSegment deallocate(Demand demand) {
		if (!owner.equals(demand)) throw new SpectrumException("Tried do deallocate segment with demand that is not its owner.");
		return new FreeSpectrumSegment(range);
	}

	@Override
	public SpectrumSegment merge(IntegerRange range, SpectrumSegment other) {
		switch(other.getType()) {
		case FreeSpectrumSegment.TYPE: return clone(range);
		case BackupSpectrumSegment.TYPE: return clone(range);
		case WorkingSpectrumSegment.TYPE: 
			WorkingSpectrumSegment castedOther = (WorkingSpectrumSegment) other;
			return new WorkingSpectrumSegment(range, castedOther.owner.getTTL() > owner.getTTL() ? castedOther.owner : owner);
		}
		return other.merge(range, this);
	}

	@Override
	public WorkingSpectrumSegment clone(IntegerRange range) {
		return new WorkingSpectrumSegment(range, owner);
	}

	@Override
	public String toString() {
		String result = super.toString();
		return result.substring(0, result.length() - 1) + ", Demand: " + owner + "}";
	}
}
