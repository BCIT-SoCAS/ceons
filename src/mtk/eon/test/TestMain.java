package mtk.eon.test;

import java.util.Arrays;
import java.util.HashSet;

import mtk.eon.net.demand.Demand;
import mtk.eon.net.spectrum.BackupSpectrumSegment;
import mtk.eon.net.spectrum.FreeSpectrumSegment;
import mtk.eon.net.spectrum.MultiSpectrumSegment;
import mtk.eon.net.spectrum.SpectrumSegment;
import mtk.eon.net.spectrum.WorkingSpectrumSegment;

public class TestMain {
	
	public static void main(String[] args) {
		MultiSpectrumSegment mss = new MultiSpectrumSegment(new FreeSpectrumSegment(0, 32));
		Demand dem0 = new DummyDemand(3);
		Demand dem1 = new DummyDemand(5);
		Demand dem2 = new DummyDemand(7);
		Demand dem3 = new DummyDemand(9);
		Demand dem4 = new DummyDemand(2);
		Demand dem5 = new DummyDemand(1);
		long time = System.currentTimeMillis();
		mss = (MultiSpectrumSegment) mss.merge(new WorkingSpectrumSegment(4, 4, dem1));
		mss = (MultiSpectrumSegment) mss.merge(new WorkingSpectrumSegment(6, 4, dem0));
		mss = (MultiSpectrumSegment) mss.merge(new BackupSpectrumSegment(16, 6, new HashSet<Demand>(Arrays.asList(dem2, dem3))));
		mss = (MultiSpectrumSegment) mss.merge(new BackupSpectrumSegment(8, 10, new HashSet<Demand>(Arrays.asList(dem4, dem5))));
		mss = (MultiSpectrumSegment) mss.merge(new WorkingSpectrumSegment(30, 4, dem1));
		time = System.currentTimeMillis() - time;
		for (SpectrumSegment segment : mss.getSegments()) System.out.println(segment);
		System.out.println("done in: " + time + "ms");
	}
}
