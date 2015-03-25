package mtk.eon.test;

import mtk.eon.net.demand.Demand;
import mtk.eon.net.spectrum.BackupSpectrumSegment;
import mtk.eon.net.spectrum.Spectrum;
import mtk.eon.net.spectrum.SpectrumSegment;
import mtk.eon.net.spectrum.WorkingSpectrumSegment;



public class TestMain {
	
	public static void main(String[] args) {
		Demand dem0 = new DummyDemand(3);
		Demand dem1 = new DummyDemand(5);
		Demand dem2 = new DummyDemand(7);
		Demand dem3 = new DummyDemand(9);
		Demand dem4 = new DummyDemand(2);
		Demand dem5 = new DummyDemand(1);
		Spectrum spectrum0 = new Spectrum(100);
		Spectrum spectrum1 = new Spectrum(100);
		Spectrum spectrum2;
		
		spectrum0.allocate(new WorkingSpectrumSegment(10, 10, dem0));
		spectrum0.allocate(new BackupSpectrumSegment(30, 16, dem1));
		spectrum0.allocate(new BackupSpectrumSegment(38, 16, dem2));
		
		spectrum1.allocate(new WorkingSpectrumSegment(15, 10, dem4));
		spectrum1.allocate(new BackupSpectrumSegment(28, 16, dem3));
		spectrum1.allocate(new BackupSpectrumSegment(40, 16, dem5));
		
		long time = System.currentTimeMillis();
		
		spectrum2 = spectrum0.merge(spectrum1);
		
		time = System.currentTimeMillis() - time;
		for (SpectrumSegment segment : spectrum2.getSegments()) System.out.println(segment);
		System.out.println("done in: " + time + "ms");
	}
}
