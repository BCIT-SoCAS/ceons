package mtk.eon.test;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFrame;

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
	
	public static void main(String[] args) throws FileNotFoundException {
		final int NODES_COUNT = 26;
		int[] uniClient = new int[NODES_COUNT * NODES_COUNT];
		int[] anyClient = new int[NODES_COUNT];
		
		Scanner s = new Scanner(new File("us26/00.ddem"));
		
		int demCount = s.nextInt();
		for (int i = 0; i < demCount; i++) {
			if (s.nextInt() == 2) {
				anyClient[s.nextInt()]++;
				s.nextInt();
				s.nextInt();
				s.nextInt();				
			} else {
				uniClient[s.nextInt() * NODES_COUNT + s.nextInt()]++;
				s.nextInt();
				s.nextInt();
			}
		}
		
		System.out.println("Anycast:");
		for (int i = 0; i < anyClient.length; i++)
			System.out.println("  " + i + ": " + anyClient[i]);

		System.out.println("Unicast:");
		for (int i = 0; i < uniClient.length; i++)
			if (i % NODES_COUNT != i / NODES_COUNT)
				System.out.println("  " + i / NODES_COUNT + "<=>" + i % NODES_COUNT + ": " + uniClient[i]);
			
		s.close();
	}
}
