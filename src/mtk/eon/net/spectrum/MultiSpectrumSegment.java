package mtk.eon.net.spectrum;

import java.util.Collection;

import mtk.eon.utils.collections.InsertionSortList;

public class MultiSpectrumSegment extends SpectrumSegment {

	InsertionSortList<SpectrumSegment> segments = new InsertionSortList<SpectrumSegment>() {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = -4843884726586180668L;

		@Override
		public int addSort(SpectrumSegment segment) {
			int result = -1;
			if (segment == null) return result;
			if (segment.getType() == Type.MULTI) for (SpectrumSegment ss : ((MultiSpectrumSegment) segment).getSegments()) {
				int tempResult = super.addSort(ss);
				if (tempResult != -1) {
					volume += ss.getVolume();
					if (result == -1) result = tempResult;
				}
			} else {
				result = super.addSort(segment);
				if (result != - 1) volume += segment.getVolume();
			}
			return result;
		}
		
		@Override
		public SpectrumSegment set(int index, SpectrumSegment element) {
			SpectrumSegment result;
			if (element.getType() == Type.MULTI) {
				result = remove(index);
				addAll(index, ((MultiSpectrumSegment) element).getSegments());
			} else {
				result = super.set(index, element);
				volume -= result.getVolume();
			}
			volume += element.getVolume();
			return result;
		}
		
		@Override
		public SpectrumSegment remove(int index) {
			SpectrumSegment result = super.remove(index);
			volume -= result.getVolume();
			return result;
		}
		
		@Override
		public boolean remove(Object segment) {
			boolean result = super.remove(segment);
			if (result) volume -= ((SpectrumSegment) segment).getVolume();
			return result;
		}
	};
	int volume;

	public MultiSpectrumSegment(Collection<SpectrumSegment> segments) {
		this.segments.addAll(segments);
	}
	
	public MultiSpectrumSegment(SpectrumSegment... segments) {
		for (SpectrumSegment segment : segments) this.segments.add(segment);
	}
	
	private MultiSpectrumSegment(InsertionSortList<SpectrumSegment> segments, int volume) {
		this.segments.addAll(segments);
		this.volume = volume;
	}
	
	public InsertionSortList<SpectrumSegment> getSegments() {
		return segments;
	}
	
	@Override
	public Type getType() {
		return Type.MULTI;
	}

	@Override
	public int getOffset() {
		return segments.get(0).getOffset();
	}

	@Override
	public int getVolume() {
		return volume;
	}

	@Override
	public boolean canOverlap(SpectrumSegment other) {
		for (SpectrumSegment segment : segments)
			if (segment.isOverlapping(other))
				if (!segment.canOverlap(other)) return false;
		return true;
	}

	@Override
	public SpectrumSegment join(SpectrumSegment other) {
		throw new SpectrumException("Joining is not supported by MultiSpectrumSegment!");
	}

	@Override
	public SpectrumSegment merge(SpectrumSegment other) {
		MultiSpectrumSegment result = new MultiSpectrumSegment(segments, volume);
		int i = 0;
		switch (other.getType()) {
		case BACKUP:
			for (; i < result.getSegments().size() && result.getSegments().get(i).getEndOffset() <= other.getOffset(); i++);
			for (; i < result.getSegments().size() && result.getSegments().get(i).getOffset() < other.getEndOffset();) {
				SpectrumSegment segment = result.getSegments().get(i);
				if (segment.getType() == Type.WORKING) {
					i++;
					continue;
				}
				segment = segment.merge(other.multiply(segment));
				result.getSegments().set(i, segment);
				i += segment.getType() == Type.MULTI ? ((MultiSpectrumSegment) segment).getSegments().size() : 1;
			}
			break;
		case MULTI:
			break;
		case WORKING:
			for (; i < result.getSegments().size() && result.getSegments().get(i).getEndOffset() <= other.getOffset(); i++);
			for (; i < result.getSegments().size() && result.getSegments().get(i).getOffset() < other.getEndOffset();) {
				SpectrumSegment segment = result.getSegments().get(i);
				segment = segment.merge(other.multiply(segment));
				result.getSegments().set(i, segment);
				i += segment.getType() == Type.MULTI ? ((MultiSpectrumSegment) segment).getSegments().size() : 1;
			}
			break;
		}
		result.getSegments().add(other.subtract(result));
		for (i = 1; i < result.getSegments().size(); i++) try {
			SpectrumSegment part = result.getSegments().get(i - 1).join(result.getSegments().get(i));
			result.getSegments().set(i - 1, part);
			result.getSegments().remove(i);
			i--;
		} catch (Exception e) {};
		return result;
	}

	@Override
	public SpectrumSegment partialClone(int offset, int volume) {
//		if (offset >= segments.last().getEndOffset()) return new FreeSpectrumSegment(offset, volume);
//		int i = 0;
//		for (; i < segments.size() && offset < segments.get(i).getOffset(); i++);
		return null;
	}
	
	@Override
	public String toString() {
		String result = super.toString();
		result = result.substring(0, result.length() - 1) + ", Segments: [";
		for (SpectrumSegment segment : segments) result += "\n  - " + segment + ", ";
		return result.substring(0, result.length() - 2) + "]}";
	}
}
