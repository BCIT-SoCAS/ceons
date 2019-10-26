package ca.bcit.net.spectrum;

import ca.bcit.net.NetworkException;
import ca.bcit.net.demand.Demand;
import ca.bcit.utils.IntegerRange;
import ca.bcit.utils.collections.InsertionSortList;

import java.util.*;

public class Spectrum {
	
	private List<SpectrumSegment> segments;
	private int slicesCount;
	private int occupiedSlices;
	
	public Spectrum(int slicesCount) {
		if (slicesCount <= 0) throw new NetworkException("The number of slices has to be larger than 0!");
		if (slicesCount % 2 != 0) throw new NetworkException("The number of slices has to be even!");
		this.slicesCount = slicesCount;
		segments = Collections.synchronizedList(new ArrayList<SpectrumSegment>() {
			private static final long serialVersionUID = 5499411539908254723L;
			@Override
			public void add(int arg0, SpectrumSegment arg1) {
				if (arg1.getRange().getLength() == 0) throw new SpectrumException("Cannot add 0 length");
				super.add(arg0, arg1);
			}
			@Override
			public boolean add(SpectrumSegment e) {
				if (e.getRange().getLength() == 0) throw new SpectrumException("Cannot add 0 length");
				return super.add(e);
			}
		});
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
	
	public int getOccupiedSlices() {
		int occupiedSlices = getSlicesCount();
		if(!segments.isEmpty()){
			synchronized (segments){
				Iterator<SpectrumSegment> it = getSegments().iterator();
				while(it.hasNext()){
					SpectrumSegment segment = it.next();
					if (segment != null && segment.getType().equals(FreeSpectrumSegment.TYPE)) {
						occupiedSlices -= segment.getRange().getLength();
					}
				}
			}
//			Iterator<SpectrumSegment> it = getSegments().iterator();
//			while(it.hasNext()){
//				SpectrumSegment segment = it.next();
//				if (segment != null && segment.getType().equals(FreeSpectrumSegment.TYPE)) {
//					occupiedSlices -= segment.getRange().getLength();
//				}
//			}
//			for (SpectrumSegment segment : segments) {
//				if (segment != null && segment.getType() == FreeSpectrumSegment.TYPE) {
//					occupiedSlices -= segment.getRange().getLength();
//				}
//			}
		}
		return occupiedSlices;
	}
	
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
		if (i == -1 || segment.getRange().getEndOffset() > slicesCount)
			throw new SpectrumException("Cannot allocate segment that is out of spectrum bounds!");

		SpectrumSegment segmentI = segments.get(i);
		if (segmentI.getRange().contains(segment.getRange())) {
			InsertionSortList<SpectrumSegment> segments = new InsertionSortList<>();
			segments.add(segment.allocate(segment.getRange().multiply(segmentI.getRange()), segmentI));
			for (IntegerRange range : segmentI.getRange().multipleSupportedSubtract(segment.getRange())) if (range.getLength() != 0)
				segments.add(segmentI.clone(range));
			this.segments.addAll(i, segments);
			this.segments.remove(i + segments.size());
			return;
		}
		for (int start = i; i < this.segments.size() && segments.get(i).range.getOffset() < segment.getRange().getEndOffset(); i++) {
			segmentI = segments.get(i);
			segments.add(i, segment.allocate(segment.getRange().multiply(segmentI.getRange()), segmentI));
			IntegerRange range = segmentI.getRange().subtract(segment.getRange());
			if (range.getLength() != 0) {
				if (i == start) segments.add(i, segmentI.clone(range));
				else segments.add(i + 1, segmentI.clone(range));
				i++;
			}
			segments.remove(i + 1);
		}
	}
	
	public void claimBackup(Demand demand) {
		for (int i = 0; i < segments.size(); i++)
			if (segments.get(i) instanceof BackupSpectrumSegment)
				if (((AllocatableSpectrumSegment) segments.get(i)).isOwnedBy(demand)) {
					Set<Demand> demands = ((BackupSpectrumSegment) segments.get(i)).getDemands();
					segments.set(i, new WorkingSpectrumSegment(segments.get(i).getRange(), demand));
					for (Demand other : demands)
						if (demand != other) other.onBackupFailure();
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
	
	public int canAllocateWorking(int volume) {
		for (SpectrumSegment segment : segments) if (segment.getType() == FreeSpectrumSegment.TYPE && segment.getRange().getLength() >= volume) return segment.getRange().getOffset();
		return -1;
	}
	
	public int canAllocateBackup(Demand demand, int volume) {
		int offset = -1, gatheredVolume = 0;
		for (int i = segments.size() - 1; i >= 0; i--)
			if (segments.get(i).getType() == FreeSpectrumSegment.TYPE || segments.get(i).getType() == BackupSpectrumSegment.TYPE && ((BackupSpectrumSegment) segments.get(i)).isDisjoint(demand)) {
				if (segments.get(i).getRange().getLength() + gatheredVolume >= volume)
					if (offset == -1) return segments.get(i).getRange().getEndOffset() - volume;
					else return offset - volume + gatheredVolume;
				else {
					offset = segments.get(i).getRange().getOffset();
					gatheredVolume += segments.get(i).getRange().getLength();
				}
			} else {
				offset = -1;
				gatheredVolume = 0;
			}
		return -1;
	}
}