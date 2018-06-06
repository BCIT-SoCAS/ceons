package ca.bcit.jfx.controllers;

import ca.bcit.ApplicationResources;
import ca.bcit.io.Logger;
import ca.bcit.io.project.Project;
import ca.bcit.io.project.ProjectFileFormat;
import ca.bcit.jfx.components.Console;
import ca.bcit.net.Network;
import ca.bcit.net.NetworkNode;
import ca.bcit.net.demand.generator.AnycastDemandGenerator;
import ca.bcit.net.demand.generator.DemandGenerator;
import ca.bcit.net.demand.generator.TrafficGenerator;
import ca.bcit.net.demand.generator.UnicastDemandGenerator;
import ca.bcit.utils.random.ConstantRandomVariable;
import ca.bcit.utils.random.MappedRandomVariable;
import ca.bcit.utils.random.UniformRandomVariable;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
			protected Void call() {
				try {
					Logger.info("Loading project from " + file.getName() + "...");
					Project project = ProjectFileFormat.getFileFormat(fileChooser.getSelectedExtensionFilter()).load(file);
					ApplicationResources.setProject(project);
					Logger.info("Finished loading project.");
					for (NetworkNode n: project.getNetwork().getNodes()){
						n.setRegeneratorsCount(100);
					}
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
			protected Void call() {
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
		
		List<MappedRandomVariable.Entry<DemandGenerator<?>>> subGenerators = new ArrayList<MappedRandomVariable.Entry<DemandGenerator<?>>>();
		
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new ConstantRandomVariable<Boolean>(false),
				new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 210, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 9, 1), new UniformRandomVariable.Integer(1, 66, 2), new UniformRandomVariable.Integer(10, 410, 10))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10),	new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(40, 410, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 9, 1), new UniformRandomVariable.Integer(1, 66, 2), new UniformRandomVariable.Integer(10, 410, 10))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1))));
		
		generators.add(new TrafficGenerator("No_Backup", new MappedRandomVariable<DemandGenerator<?>>(subGenerators)));
		
		subGenerators = new ArrayList<MappedRandomVariable.Entry<DemandGenerator<?>>>();
		
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new ConstantRandomVariable<Boolean>(false),
				new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 210, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 9, 1), new UniformRandomVariable.Integer(1, 66, 2), new UniformRandomVariable.Integer(10, 410, 10))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(40, 410, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 9, 1), new UniformRandomVariable.Integer(1, 66, 2), new UniformRandomVariable.Integer(10, 410, 10))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1))));
		
		generators.add(new TrafficGenerator("Direct_Backup", new MappedRandomVariable<DemandGenerator<?>>(subGenerators)));
		
		subGenerators = new ArrayList<MappedRandomVariable.Entry<DemandGenerator<?>>>();

		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new ConstantRandomVariable<Boolean>(false),
				new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 210, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 9, 1), new UniformRandomVariable.Integer(1, 66, 2), new UniformRandomVariable.Integer(10, 410, 10))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
				new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")),
				new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(40, 410, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 9, 1), new UniformRandomVariable.Integer(1, 66, 2), new UniformRandomVariable.Integer(10, 410, 10))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")),
				new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
				new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1))));
		
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new ConstantRandomVariable<Boolean>(false),
				new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 210, 10), new ConstantRandomVariable<Float>(0.5f), new UniformRandomVariable.Integer(1, 9, 1), new UniformRandomVariable.Integer(1, 66, 2), new UniformRandomVariable.Integer(10, 410, 10))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<Float>(0.5f), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(40, 410, 10), new ConstantRandomVariable<Float>(0.5f), new UniformRandomVariable.Integer(1, 9, 1), new UniformRandomVariable.Integer(1, 66, 2), new UniformRandomVariable.Integer(10, 410, 10))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<Float>(0.5f), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
				new ConstantRandomVariable<Boolean>(false),	new ConstantRandomVariable<Boolean>(true), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<Float>(0.5f), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1))));
		
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new ConstantRandomVariable<Boolean>(true),
				new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 210, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 9, 1), new UniformRandomVariable.Integer(1, 66, 2), new UniformRandomVariable.Integer(10, 410, 10))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
				new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("replicas")),
				new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(40, 410, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 9, 1), new UniformRandomVariable.Integer(1, 66, 2), new UniformRandomVariable.Integer(10, 410, 10))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()), new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")),
				new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1))));
		subGenerators.add(new MappedRandomVariable.Entry<DemandGenerator<?>>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<NetworkNode>(network.getGroup("international")), new UniformRandomVariable.Generic<NetworkNode>(network.getNodes()),
				new ConstantRandomVariable<Boolean>(true),	new ConstantRandomVariable<Boolean>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<Float>(1f), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1), new UniformRandomVariable.Integer(1, 2, 1))));
		
		generators.add(new TrafficGenerator("Classed", new MappedRandomVariable<DemandGenerator<?>>(subGenerators)));
			
		SimulationMenuController.generatorsStatic.setItems(new ObservableListWrapper<TrafficGenerator>(generators));
	}

	private int i;
	@FXML public void testButton(ActionEvent e) {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() {
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
