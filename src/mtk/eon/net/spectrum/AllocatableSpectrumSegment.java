package mtk.eon.net.spectrum;

import mtk.eon.net.demand.Demand;
import mtk.eon.utils.IntegerRange;

public abstract class AllocatableSpectrumSegment extends SpectrumSegment {

	public AllocatableSpectrumSegment(IntegerRange range) {
		super(range);
	}
	
	public abstract boolean isOwnedBy(Demand demand);
	
	public abstract int getOccupationTimeLeft();
	
	public abstract boolean canAllocate(SpectrumSegment other);

	public abstract SpectrumSegment allocate(IntegerRange range, SpectrumSegment other);
	
	public abstract SpectrumSegment deallocate(Demand demand);
}
