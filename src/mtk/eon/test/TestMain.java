package mtk.eon.test;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import mtk.eon.net.NetworkNode;
import mtk.eon.net.demand.AnycastDemand;
import mtk.eon.net.demand.Demand;
import mtk.eon.net.demand.UnicastDemand;
import mtk.eon.net.demand.generator.AnycastDemandGenerator;
import mtk.eon.net.demand.generator.DemandGenerator;
import mtk.eon.net.demand.generator.TrafficGenerator;
import mtk.eon.net.demand.generator.UnicastDemandGenerator;
import mtk.eon.utils.random.ConstantRandomVariable;
import mtk.eon.utils.random.IrwinHallRandomVariable;
import mtk.eon.utils.random.MappedRandomVariable;
import mtk.eon.utils.random.MappedRandomVariable.Entry;
import mtk.eon.utils.random.UniformRandomVariable;

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
		int erlang = 300, demandsCount = 150281, erlmin = 300 - 50, erlmax = 300 + 50;
		List<NetworkNode> all = new ArrayList<NetworkNode>();
		List<NetworkNode> reps = new ArrayList<NetworkNode>();
		List<NetworkNode> inters = new ArrayList<NetworkNode>();
		
		for (int i = 0; i < 28; i++) all.add(new NetworkNode("" + i));
		for (Integer i : Arrays.asList(0, 1, 3, 5, 8, 16, 24)) reps.add(all.get(i));
		for (Integer i : Arrays.asList(17, 19, 21)) inters.add(all.get(i));
		
		List<Entry<DemandGenerator<?>>> subGenerators = new ArrayList<Entry<DemandGenerator<?>>>();
		
		subGenerators.add(new Entry<DemandGenerator<?>>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(all), new ConstantRandomVariable<Boolean>(false),
				new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 210, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 3), new ConstantRandomVariable<Float>(1f))));
		subGenerators.add(new Entry<DemandGenerator<?>>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(all), new UniformRandomVariable.Generic<NetworkNode>(all),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 3),
				new ConstantRandomVariable<Float>(1f))));
		subGenerators.add(new Entry<DemandGenerator<?>>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(reps), new UniformRandomVariable.Generic<NetworkNode>(reps),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(40, 410, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 3),
				new ConstantRandomVariable<Float>(1f))));
		subGenerators.add(new Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(all), new UniformRandomVariable.Generic<NetworkNode>(inters),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 3),
				new ConstantRandomVariable<Float>(1f))));
		subGenerators.add(new Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(inters), new UniformRandomVariable.Generic<NetworkNode>(all),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 3),
				new ConstantRandomVariable<Float>(1f))));
		
		MappedRandomVariable<DemandGenerator<?>> distribution = new MappedRandomVariable<DemandGenerator<?>>(subGenerators);
		TrafficGenerator generator = new TrafficGenerator("No failure-ERL" + erlang, distribution);
		
		generator.setSeed(10);
		
		int[] pdf = new int[28 * 28];
		
		for (int i = 0; i < demandsCount; i++) {
			System.out.println(i);
			Demand demand = generator.next();
			if (demand instanceof AnycastDemand) generator.next();
			else
				pdf[Integer.parseInt(((UnicastDemand) demand).getSource().getName()) * 28 + Integer.parseInt(((UnicastDemand) demand).getDestination().getName())]++;
		}
		
		for (int i = 0; i < 28 * 28; i++)
			if (i / 28 != i % 28)
				System.out.println(i / 28 + "=>" + i % 28 + ": " + pdf[i]);
	}
}
