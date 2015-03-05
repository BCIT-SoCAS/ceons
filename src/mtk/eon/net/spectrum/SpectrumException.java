package mtk.eon.net.spectrum;

public class SpectrumException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6041497211786989584L;

	public enum Type {
		DIFFERENT_SPECTRA_SEGMENTS_MERGE ("Cannot merge SpectrumSegments which belong to different Spectra."),
		NOT_ADJACENT_SEGMENTS_MERGE ("Cannot merge SpectrumSegments which are not adjacent."),
		DEALLOCATING_UNALLOCATED ("Cannot deallocate SpectrumSegment which was not previously allocated."),
		OVERLAPPING_WORKING_SEGMENT ("Working SpectrumSegments cannot overlap."),
		MERGE_DIFFERENT_LENGTH_SPECTRA ("Cannot merge spectra of different lengths.");
		
		private String message;
		
		Type (String message) {
			this.message = message;
		}
	}
	
	Type type;
	
	public SpectrumException(Type type) {
		super(type.message);
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
}
