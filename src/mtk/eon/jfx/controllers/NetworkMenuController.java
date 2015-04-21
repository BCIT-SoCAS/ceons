package mtk.eon.jfx.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import mtk.eon.ApplicationResources;
import mtk.eon.io.Logger;
import mtk.eon.io.project.Project;
import mtk.eon.io.project.ProjectFileFormat;
import mtk.eon.jfx.components.Console;
import mtk.eon.net.Network;
import mtk.eon.net.NetworkNode;
import mtk.eon.net.NetworkPath;
import mtk.eon.net.demand.generator.AnycastDemandGenerator;
import mtk.eon.net.demand.generator.DemandGenerator;
import mtk.eon.net.demand.generator.TrafficGenerator;
import mtk.eon.net.demand.generator.UnicastDemandGenerator;
import mtk.eon.utils.random.ConstantRandomVariable;
import mtk.eon.utils.random.IrwinHallRandomVariable;
import mtk.eon.utils.random.MappedRandomVariable;
import mtk.eon.utils.random.MappedRandomVariable.Entry;
import mtk.eon.utils.random.UniformRandomVariable;

import com.sun.javafx.collections.ObservableListWrapper;

public class NetworkMenuController {

	@FXML public void onNew(ActionEvent e) {
		Logger.debug("new");
	}
	
	@FXML public void onLoad(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(ProjectFileFormat.getExtensionFilters());
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		final File file = fileChooser.showOpenDialog(null);
		
		if (file == null) return;
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {
					Logger.info("Loading project from " + file.getName() + "...");
					Project project = ProjectFileFormat.getFileFormat(fileChooser.getSelectedExtensionFilter()).load(file);
					ApplicationResources.setProject(project);
					Logger.info("Finished loading project.");
					
					setupGenerators(project); // TODO TEMPORARY
					
				} catch (Exception ex) {
					Logger.info("An exception occurred while loading the project.");
					Logger.debug(ex);
				}
				return null;
			}
		};
		task.run();
	}
	
	@FXML public void onSave(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(ProjectFileFormat.getExtensionFilters());
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		final File file = fileChooser.showSaveDialog(null);
		
		if (file == null) return;
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {
					Logger.info("Saving project to " + file.getName() + "...");
					ProjectFileFormat.getFileFormat(fileChooser.getSelectedExtensionFilter()).save(file, ApplicationResources.getProject());
					Logger.info("Finished saving project.");
				} catch (Exception ex) {
					Logger.info("An exception occurred while saving the project.");
					Logger.debug(ex);
				}
				return null;
			}
		};
		task.run();
	}
	
	private void setupGenerators(Project project) {
		Network network = project.getNetwork();
		List<TrafficGenerator> generators = project.getTrafficGenerators();
		
		for (int erlang = 300; erlang < 1300; erlang += 100) {
			int erlmin = erlang - 50, erlmax = erlang + 50;
			List<Entry<DemandGenerator<?>>> subGenerators = new ArrayList<Entry<DemandGenerator<?>>>();
			
			subGenerators.add(new Entry<DemandGenerator<?>>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new ConstantRandomVariable<Boolean>(false),
					new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 210, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10), new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
					new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")),
					new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(40, 410, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")),
					new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
					new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			
			MappedRandomVariable<DemandGenerator<?>> distribution = new MappedRandomVariable<DemandGenerator<?>>(subGenerators);
			generators.add(new TrafficGenerator("No failure-ERL" + erlang, distribution));
		}
		
		for (int erlang = 300; erlang < 1300; erlang += 100) {
			int erlmin = erlang - 50, erlmax = erlang + 50;
			List<Entry<DemandGenerator<?>>> subGenerators = new ArrayList<Entry<DemandGenerator<?>>>();
			
			subGenerators.add(new Entry<DemandGenerator<?>>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new ConstantRandomVariable<Boolean>(false),
					new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 210, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10), new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
					new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")),
					new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(40, 410, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")),
					new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
					new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			
			MappedRandomVariable<DemandGenerator<?>> distribution = new MappedRandomVariable<DemandGenerator<?>>(subGenerators);
			generators.add(new TrafficGenerator("1-1-Backup-ERL" + erlang, distribution));
		}
		
		for (int erlang = 300; erlang < 1300; erlang += 100) {
			int erlmin = erlang - 50, erlmax = erlang + 50;
			List<Entry<DemandGenerator<?>>> subGenerators = new ArrayList<Entry<DemandGenerator<?>>>();

			subGenerators.add(new Entry<DemandGenerator<?>>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new ConstantRandomVariable<Boolean>(false),
					new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 210, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10), new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
					new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")),
					new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(40, 410, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")),
					new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
					new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			
			subGenerators.add(new Entry<DemandGenerator<?>>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new ConstantRandomVariable<Boolean>(false),
					new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 210, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10), new ConstantRandomVariable<Float>(0.5f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
					new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(0.5f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")),
					new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(40, 410, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(0.5f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")),
					new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(0.5f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
					new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(0.5f))));
			
			subGenerators.add(new Entry<DemandGenerator<?>>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new ConstantRandomVariable<Boolean>(true),
					new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 210, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10), new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
					new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")),
					new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(40, 410, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")),
					new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			subGenerators.add(new Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
					new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new IrwinHallRandomVariable.Integer(erlmin, erlmax, 10),
					new ConstantRandomVariable<Float>(1f))));
			
			MappedRandomVariable<DemandGenerator<?>> distribution = new MappedRandomVariable<DemandGenerator<?>>(subGenerators);
			generators.add(new TrafficGenerator("3-Class-ERL" + erlang, distribution));
		}
		SimulationMenuController.generatorsStatic.setItems(new ObservableListWrapper<TrafficGenerator>(generators));
			
	}

	int i;
	@FXML public void testButton(ActionEvent e) {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				Network network = ApplicationResources.getProject().getNetwork();
				
				i = 1;
				try {
					network.maxPathsCount = network.calculatePaths(() -> updateProgress(i++, network.getNodesPairsCount()));
				} catch (Throwable e) {
					e.printStackTrace();
				}
				Console.cout.println("Max best paths count: " + network.maxPathsCount);
				
				return null;
			}
			
			
		};
		SimulationMenuController.progressBar.runTask(task, true);
	}
}
