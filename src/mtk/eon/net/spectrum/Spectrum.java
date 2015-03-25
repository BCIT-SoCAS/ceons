package mtk.eon.net.spectrum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mtk.eon.net.NetworkException;
import mtk.eon.net.demand.Demand;
import mtk.eon.utils.IntegerRange;
import mtk.eon.utils.collections.InsertionSortList;

public class Spectrum {
	
	List<SpectrumSegment> segments;
	int slicesCount;
	int occupiedSlices;
	
	public Spectrum(int slicesCount) {
		if (slicesCount <= 0) throw new NetworkException("The number of slices has to be larger than 0!");
		if (slicesCount % 2 != 0) throw new NetworkException("The number of slices has to be even!");
		this.slicesCount = slicesCount;
		segments = new ArrayList<>();
		segments.add(new FreeSpectrumSegment(0, slicesCount));
	}
	
	private Spectrum(List<SpectrumSegment> segments, int slicesCount) {
		this.segments = segments;
		this.slicesCount = slicesCount;
		if (segments.size() != 0 && segments.get(0) instanceof AllocatableSpectrumSegment)
			occupiedSlices = segments.get(0).getRange().getLength();
		for (int i = 1; i < segments.size(); i++) {
			SpectrumSegment segment = segments.get(i);
			if (segment instanceof AllocatableSpectrumSegment) occupiedSlices += segment.getRange().getLength();
			try {
				segments.set(i - 1, segment.join(segments.get(i - 1)));
				segments.remove(i);
				i--;
			} catch (SpectrumException e) {}
		}
	}
	
	public List<SpectrumSegment> getSegments() {
		return Collections.unmodifiableList(segments);
	}
	
	public int getSlicesCount() {
		return slicesCount;
	}
	
//	public int getOccupiedSlices(SpectrumSegment candidate) {
//		if (candidate.getType().equals(WorkingSpectrumSegment.TYPE)) return occupiedSlices;
//		else if (candidate.getType().equals(BackupSpectrumSegment.TYPE)) {
//			int result = 0;
//			for (SpectrumSegment segment : segments.getSegments()) if (segment.getType().equals(FreeSpectrumSegment.TYPE) ||
//					segment.getType().equals(BackupSpectrumSegment.TYPE) && segment.canOverlap(candidate))
//				result += segment.range.getLength();
//			return result;
//		} else throw new SpectrumException("Occupied slices check can only be performed on WORKING or BACKUP segments.");
//	}
	
	private int firstOverlapIndex(int min, int max, SpectrumSegment segment) {
		if (min == max) return -1;
		int mid = (max - min) / 2 + min;
		if (segment.getRange().getOffset() >= segments.get(mid).getRange().getEndOffset()) return firstOverlapIndex(mid + 1, max, segment);
		else if (segment.getRange().getOffset() < segments.get(mid).getRange().getOffset()) return firstOverlapIndex(min, mid, segment);
		else return mid;
	}
	
	public boolean canAllocate(AllocatableSpectrumSegment segment) {
		int i = firstOverlapIndex(0, segments.size(), segment);
		if (i == -1 || segment.getRange().getEndOffset() > slicesCount) return false;
		for (; i < segments.size() && segments.get(i).getRange().getOffset() < segment.getRange().getOffset(); i++)
		for (SpectrumSegment s : segments) if (s.getRange().isOverlapping(segment.getRange()) && !segment.canAllocate(s)) return false;
		return true;
	}
	
	public Spectrum merge(Spectrum other) {
		if (slicesCount != other.slicesCount) throw new SpectrumException("Cannot merge spectra that are not equal size.");
		List<SpectrumSegment> segments = new ArrayList<>();
		for (int i = 0, j = 0; i < this.segments.size() && j < other.segments.size();) {
			SpectrumSegment segmentI = this.segments.get(i), segmentJ = other.segments.get(j);
			if (segmentI.getRange().isOverlapping(segmentJ.getRange()))
				segments.add(segmentI.merge(segmentI.getRange().multiply(segmentJ.getRange()), segmentJ));
			if (segmentI.getRange().getEndOffset() < segmentJ.getRange().getEndOffset()) i++;
			else j++;
		}
		return new Spectrum(segments, slicesCount);
	}
	
	public void allocate(AllocatableSpectrumSegment segment) {
		int i = firstOverlapIndex(0, segments.size(), segment);
		if (i == -1 || segment.getRange().getEndOffset() > slicesCount) throw new SpectrumException("Cannot allocate segment that is out of spectrum bounds!");
		SpectrumSegment segmentI = segments.get(i);
		if (segmentI.getRange().contains(segment.getRange())) {
			InsertionSortList<SpectrumSegment> segments = new InsertionSortList<>();
			segments.add(segment.allocate(segment.getRange().multiply(segmentI.getRange()), segmentI));
			for (IntegerRange range : segmentI.getRange().multipleSupportedSubtract(segment.getRange())) if (range != null)
				segments.add(segmentI.clone(range));
			this.segments.addAll(i, segments);
			this.segments.remove(i + segments.size());
			return;
		}
		for (int start = i; i < this.segments.size() && segments.get(i).range.getOffset() < segment.getRange().getEndOffset(); i++) {
			segmentI = segments.get(i);
			segments.add(i, segment.allocate(segment.getRange().multiply(segmentI.getRange()), segmentI));
			IntegerRange range = segmentI.getRange().subtract(segment.getRange());
			if (range != null) {
				if (i == start) segments.add(i, segmentI.clone(range));
				else segments.add(i + 1, segmentI.clone(range));
				i++;
			}
			segments.remove(i + 1);
		}
	}
	
	public void deallocate(Demand demand) {
		for (int i = 0; i < segments.size(); i++) if (segments.get(i) instanceof AllocatableSpectrumSegment && ((AllocatableSpectrumSegment) segments.get(i)).isOwnedBy(demand)) {
			segments.set(i, ((AllocatableSpectrumSegment) segments.get(i)).deallocate(demand));
			if (i != 0) try {
				segments.set(i - 1, segments.get(i - 1).join(segments.get(i)));
				segments.remove(i);
				i--;
			} catch (SpectrumException e) {}
			if (i != segments.size() - 1) try {
				segments.set(i, segments.get(i).join(segments.get(i + 1)));
				segments.remove(i + 1);
			} catch (SpectrumException e) {}
		}
	}
	
	public boolean canAllocateWorking(int volume) {
		for (SpectrumSegment segment : segments) if (segment.getType() == FreeSpectrumSegment.TYPE && segment.getRange().getLength() >= volume) return true;
		return false;
	}
}