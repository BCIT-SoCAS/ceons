package mtk.eon.utils;


public class IntegerRange {

	public static final IntegerRange EMPTY = new IntegerRange(0, 0);
	
	private int offset, length;
	
	public IntegerRange(int offset, int length) {
		if (length < 0) throw new IntegerRangeException("Cannot create a range with negative length.");
		this.offset = offset;
		this.length = length;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getEndOffset() {
		return offset + length;
	}
	
	public boolean contains(IntegerRange other) {
		return offset <= other.offset && getEndOffset() >= other.getEndOffset();
	}
	
	public boolean isAdjacent(IntegerRange other) {
		return offset == other.getEndOffset() || other.offset == getEndOffset();
	}
	
	public boolean isOverlapping(IntegerRange other) {
		return !(offset >= other.getEndOffset() || other.offset >= getEndOffset());
	}
	
	public boolean isDisconnected(IntegerRange other) {
		return offset > other.getEndOffset() || other.offset > other.getEndOffset();
	}
	
	public IntegerRange add(IntegerRange other) {
		if (isDisconnected(other)) throw new IntegerRangeException("Cannot add ranges that are disconnected!");
		else return new IntegerRange(Math.min(offset, other.offset), length + other.length);
	}
	
	public IntegerRange multiply(IntegerRange other) {
		if (isOverlapping(other)) {
			int offset = Math.max(this.offset, other.offset);
			return new IntegerRange(offset, Math.min(getEndOffset(), other.getEndOffset()) - offset);
		} else return EMPTY;
	}
	
	public IntegerRange subtract(IntegerRange other) {
		if (isOverlapping(other)) {
			if (offset >= other.offset)
				if (getEndOffset() <= other.getEndOffset()) return EMPTY;
				else return new IntegerRange(other.getEndOffset(), getEndOffset() - other.getEndOffset());
			else
				if (getEndOffset() <= other.getEndOffset()) return new IntegerRange(offset, other.offset - offset);
				else throw new IntegerRangeException("Subtraction results in discontinuous range which is not supported by this method. Instead use multipleSupportedSubtract(IntegerRange other).");
		} else return this;
	}
	
	public IntegerRange[] multipleSupportedSubtract(IntegerRange other) {
		IntegerRange a = new IntegerRange(offset, other.offset - offset);
		IntegerRange b = new IntegerRange(other.getEndOffset(), getEndOffset() - other.getEndOffset());
		if (a != null)
			if (b != null) return new IntegerRange[] {a, b};
			else return new IntegerRange[] {a};
		else
			if (b != null) return new IntegerRange[] {b};
			else return new IntegerRange[] {};
	}
	
	@Override
	public String toString() {
		return "{Offset: " + offset + ", Length: " + length + "}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + length;
		result = prime * result + offset;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntegerRange other = (IntegerRange) obj;
		if (length != other.length)
			return false;
		if (offset != other.offset)
			return false;
		return true;
	}
	
	public static int binarySearch(IntegerRange[] array, int key) {
		int min = 0, max = array.length - 1;
		while (min <= max) {
			int mid = (min + max) >>> 1;
			IntegerRange midValue = array[mid];
			if (midValue.getEndOffset() <= key) min = mid + 1;
			else if (midValue.getOffset() > key) max = mid - 1;
			else return mid;
		}
		return -min - 1;
	}
}
