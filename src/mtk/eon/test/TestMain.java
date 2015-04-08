package mtk.eon.test;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import mtk.eon.utils.random.IrwinHallRandomVariable;
import mtk.eon.utils.random.MappedRandomVariable;

public class TestMain {
	
	public static void pdf(int[] pdf, float height) {
		JFrame frame = new JFrame("pdf");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Canvas canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(pdf.length < 400 ? 400 : pdf.length, 600));
		frame.add(canvas);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		canvas.createBufferStrategy(2);
		long lastFrame = System.nanoTime();
		while (true) {
			if (System.nanoTime() - lastFrame < 1000000000.0 / 30.0) continue;
			lastFrame = System.nanoTime();
			Graphics g = canvas.getBufferStrategy().getDrawGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			g.setColor(Color.WHITE);
			for (int i = 0; i < pdf.length; i++) g.drawLine(i, 600, i, 600 - (int) (pdf[i] * height));
			g.dispose();
			canvas.getBufferStrategy().show();
		}
	}
	
	public static void main(String[] args) {
//		Demand dem0 = new DummyDemand(3);
//		Demand dem1 = new DummyDemand(5);
//		Demand dem2 = new DummyDemand(7);
//		Demand dem3 = new DummyDemand(9);
//		Demand dem4 = new DummyDemand(2);
//		Demand dem5 = new DummyDemand(1);
//		Spectrum spectrum0 = new Spectrum(100);
//		Spectrum spectrum1 = new Spectrum(100);
//		Spectrum spectrum2;
//		
//		spectrum0.allocate(new WorkingSpectrumSegment(10, 10, dem0));
//		spectrum0.allocate(new BackupSpectrumSegment(30, 16, dem1));
//		spectrum0.allocate(new BackupSpectrumSegment(38, 16, dem2));
//		
//		spectrum1.allocate(new WorkingSpectrumSegment(15, 10, dem4));
//		spectrum1.allocate(new BackupSpectrumSegment(28, 16, dem3));
//		spectrum1.allocate(new BackupSpectrumSegment(40, 16, dem5));
//		
//		long time = System.currentTimeMillis();
//		
//		spectrum2 = spectrum0.merge(spectrum1);
//		
//		time = System.currentTimeMillis() - time;
//		for (SpectrumSegment segment : spectrum2.getSegments()) System.out.println(segment);
//		System.out.println("done in: " + time + "ms");
		
//		Random r = new Random(15);
//		
//		int[] pdf = new int[15 * 15];
//		
//		long time = System.nanoTime();
//		for (int i = 0; i < 100000; i++) pdf[r.nextInt(15) * 15 + r.nextInt(15)]++;
//		time = System.nanoTime() - time;
//		System.out.println("Time: " + time / 100000);
//		
//		pdf(pdf, 0.1f);
		
		System.out.println("a".split(".").length);
	}
}
