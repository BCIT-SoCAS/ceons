package mtk.eon.net;

public enum Modulation {
	
	BPSK, QPSK, QAM8, QAM16, QAM32, QAM64;
	
	public int[] modulationDistances = new int[40];
	public int[] slicesConsumption = new int[40];
}
