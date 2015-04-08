package mtk.eon.utils.random;


public abstract class IrwinHallRandomVariable<N> extends RandomVariable<N> {
	
	protected int rank;
	
	public IrwinHallRandomVariable(long seed, int rank) {
		super(seed);
		this.rank = rank;
	}
	
	public static class Integer extends IrwinHallRandomVariable<java.lang.Integer> {

		int offset, width;
		
		public Integer(long seed, int min, int max, int rank) {
			super(seed, rank);
			offset = min;
			width = max - min;
		}

		@Override
		public java.lang.Integer next() {
			int result = 0;
			for (int i = 0; i < rank; i++) result += generator.nextInt(width);
			return result / rank + offset;
		}
	}
}
