package ca.bcit.net.spectrum;

import ca.bcit.net.demand.Demand;
import ca.bcit.utils.IntegerRange;

public abstract class AllocatableSpectrumSegment extends SpectrumSegment {
	AllocatableSpectrumSegment(IntegerRange range) {
		super(range);
	}
	
	public abstract boolean isOwnedBy(Demand demand);
	
	public abstract int getOccupationTimeLeft();
	
	public abstract boolean canAllocate(SpectrumSegment other);

	public abstract SpectrumSegment allocate(IntegerRange range, SpectrumSegment other);
	
	public abstract SpectrumSegment deallocate(Demand demand);
}
