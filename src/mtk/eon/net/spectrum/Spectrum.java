package mtk.eon.net.spectrum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mtk.eon.net.NetworkException;
import mtk.eon.net.demand.Demand;
import mtk.general.InsertionSortList;

public class Spectrum {
	
	public static final SpectrumSegment CANNOT_ALLOCATE = new SpectrumSegment(null, -1, -1);
	
	private InsertionSortList<SpectrumSegment> spectrum = new InsertionSortList<SpectrumSegment>();
	int slicesCount, occupiedSlices;
	
	public Spectrum(int slicesCount) {
		if (slicesCount <= 0) throw new NetworkException("The number of slices has to be larger than 0!");
		if (slicesCount % 2 != 0) throw new NetworkException("The number of slices has to be even!");
		this.slicesCount = slicesCount;
		spectrum.add(new SpectrumSegment(this, 0, slicesCount, true));
	}
	
	public int getSlicesCount() {
		return slicesCount;
	}
	
	public void merge(Spectrum spectrum) {
		if (slicesCount != getSlicesCount())
			throw new SpectrumException(SpectrumException.Type.MERGE_DIFFERENT_LENGTH_SPECTRA);
		
	}
	
	public List<SpectrumSegment> getWorkingCandidates(int volume) {
		List<SpectrumSegment> candidates = new ArrayList<SpectrumSegment>();
		for (SpectrumSegment segment : spectrum) if (segment.getVolume() >= volume && segment.isFree()) candidates.add(segment);
		return candidates;
	}
	
	public List<List<SpectrumSegment>> getBackupCandidate(int volume, Demand demand) {
		List<List<SpectrumSegment>> candidates = new ArrayList<List<SpectrumSegment>>();
		candidates.add(new ArrayList<SpectrumSegment>());
		for (SpectrumSegment segment : spectrum) {
			List<SpectrumSegment> lastCluster = candidates.get(candidates.size() - 1);
			if (segment.isFree() || segment instanceof BackupSpectrumSegment && ((BackupSpectrumSegment) segment).canOverlap(demand))
				if (lastCluster.isEmpty() || lastCluster.get(lastCluster.size() - 1).getEndOffset() == segment.getOffset()) lastCluster.add(segment);
				else {
					lastCluster = new ArrayList<SpectrumSegment>();
					lastCluster.add(segment);
					candidates.add(lastCluster);
				}
			else
				if (!lastCluster.isEmpty()) candidates.add(new ArrayList<SpectrumSegment>());
		}
		return candidates;
	}
	
	public List<SpectrumSegment> getFullSpectrum() {
		return new InsertionSortList<SpectrumSegment>(spectrum);
	}
	
	public boolean canAllocateWorking(int volume) {
		for (SpectrumSegment segment : spectrum) if (segment.getVolume() >= volume) return true;
		return false;
	}
	
	public SpectrumSegment allocateWorking(SpectrumSegment segment) {
		int start, end;
		for (start = 0; segment.getOffset() < spectrum.get(start).getOffset(); start++);
		for (end = start; segment.getEndOffset() > spectrum.get(end).getEndOffset(); end++)
			if (!spectrum.get(end).isFree()) throw new SpectrumException(SpectrumException.Type.OVERLAPPING_WORKING_SEGMENT);
		if () // TODO
		return CANNOT_ALLOCATE;
	}
	
	public SpectrumSegment allocateOverlapping(int volume, Demand demand) {
		InsertionSortList<SpectrumSegment> tempFree = new InsertionSortList<SpectrumSegment>(free);
		for (BackupSpectrumSegment backupSegment : backup) if (backupSegment.canOverlap(demand)) tempFree.add(backupSegment);
		ArrayList<SpectrumSegment> best, candidate = new ArrayList<SpectrumSegment>();
		int 
		for (SpectrumSegment segment : tempFree) {
			if (candidate.isEmpty() || candidate.get(candidate.size() - 1).getEndOffset() == segment.getOffset())
				;
			else
				;
		}
		return null; // TODO overlapping allocation
	}
	
	public void deallocate(SpectrumSegment segment) {
		int index = main.indexOf(segment);
		if (index == -1) throw new SpectrumException(SpectrumException.Type.DEALLOCATING_UNALLOCATED);
		main.remove(index);
		index = free.addSort(segment);
		if (index != free.size()) if (segment.isAdjacent(free.get(index + 1))) {
			free.set(index, segment.merge(free.get(index + 1)));
			free.remove(index + 1);
		}
		if (index != 0) if (segment.isAdjacent(free.get(index - 1))) {
			free.set(index, segment.merge(free.get(index - 1)));
			free.remove(index - 1);
		}
		occupiedSlices -= segment.getVolume();
	}
	
	public int getOccupiedSlices() {
		return occupiedSlices;
	}
	
	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < slices.length; i++)
			result += String.format("%64s", Long.toBinaryString(slices[i])).replace(' ', '0') + 
			(i != slices.length - 1 ? "\n" : "");
		return result;
	}
}
