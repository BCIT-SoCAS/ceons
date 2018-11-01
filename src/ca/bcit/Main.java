package ca.bcit;

import ca.bcit.io.Logger;
import ca.bcit.io.YamlSerializable;
import ca.bcit.io.project.EONProject;
import ca.bcit.io.project.EONProjectFileFormat;
import ca.bcit.io.project.Project;
import ca.bcit.io.project.ProjectFileFormat;
import ca.bcit.jfx.controllers.SimulationMenuController;
import ca.bcit.jfx.tasks.SimulationTask;
import ca.bcit.net.*;
import ca.bcit.net.algo.AMRA;
import ca.bcit.net.demand.generator.AnycastDemandGenerator;
import ca.bcit.net.demand.generator.DemandGenerator;
import ca.bcit.net.demand.generator.TrafficGenerator;
import ca.bcit.net.demand.generator.UnicastDemandGenerator;
import ca.bcit.utils.random.ConstantRandomVariable;
import ca.bcit.utils.random.IrwinHallRandomVariable;
import ca.bcit.utils.random.MappedRandomVariable;
import ca.bcit.utils.random.UniformRandomVariable;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

	private static long seed = 0;
	private static int demandsCount = 10000;
	private static int erlang = 300;
	private static double alpha = 0;
	private static boolean replicaPreservation = false;
	private static List<TrafficGenerator> generators = new ArrayList<>();
	private static int i;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("/ca/bcit/jfx/res/MainWindow.fxml"));
		GridPane root = (GridPane)loader.load();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setMinWidth(primaryStage.getWidth());
		primaryStage.setMinHeight(primaryStage.getHeight());
		
		final Canvas canvas = (Canvas) scene.lookup("#graph");
		BorderPane pane = (BorderPane) scene.lookup("#borderPane");
		canvas.widthProperty().bind(pane.widthProperty());
		canvas.heightProperty().bind(pane.heightProperty());
		System.out.println(canvas.getBoundsInParent());
//		canvas.getGraphicsContext2D().fillRect(10, 10, 20, 20);
//		canvas.setOnMouseDragged(e -> { canvas.getGraphicsContext2D().fillRect(e.getX(), e.getY(), 10, 10); });
	}
	
	public static void main(String[] args) {
		try {
			YamlSerializable.registerSerializableClass(NetworkNode.class);
			YamlSerializable.registerSerializableClass(NetworkLink.class);
			YamlSerializable.registerSerializableClass(Network.class);
			
			YamlSerializable.registerSerializableClass(MappedRandomVariable.class);
			YamlSerializable.registerSerializableClass(UniformRandomVariable.Generic.class);
			YamlSerializable.registerSerializableClass(ConstantRandomVariable.class);
			YamlSerializable.registerSerializableClass(IrwinHallRandomVariable.Integer.class);
			YamlSerializable.registerSerializableClass(UnicastDemandGenerator.class);
			YamlSerializable.registerSerializableClass(AnycastDemandGenerator.class);
			YamlSerializable.registerSerializableClass(TrafficGenerator.class);

			ProjectFileFormat.registerFileFormat(new EONProjectFileFormat());
			EONProjectFileFormat project = new EONProjectFileFormat();
			File file = new File("euro28.eon"); //file to change
			Project eon = project.load(file);
			ApplicationResources.setProject(eon);
			for (NetworkNode n: eon.getNetwork().getNodes()){
				n.setRegeneratorsCount(100); //change number of regenerators
			}
			generators = setupGenerators(eon);
			Network network = eon.getNetwork();

			network.setDemandAllocationAlgorithm(new AMRA()); //here to set algorithm

			network.setCanSwitchModulation(true);
			network.setModualtionMetricType(MetricType.DYNAMIC);

			for (Modulation modulation : Modulation.values()) {
				network.allowModulation(modulation);
			}
			network.setRegeneratorMetricValue(5);
			network.setRegeneratorMetricType(MetricType.STATIC);

			Simulation simulation = new Simulation(network, generators.get(0));
			SimulationTask task = new SimulationTask(simulation, seed, alpha, erlang, demandsCount, replicaPreservation);
			i = 1;
			try {
				network.maxPathsCount = network.calculatePaths(() -> task.updateProgress(i++, network.getNodesPairsCount()));
			} catch (Throwable e) {
				e.printStackTrace();
			}
			network.setBestPathsCount(10);
			simulation.simulate(seed, demandsCount, alpha, erlang, replicaPreservation,	task);
//			below is GUI code
//			ProjectFileFormat.registerFileFormat(new EONProjectFileFormat());
//			launch(args);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Fatal error occured: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private static List<TrafficGenerator> setupGenerators(Project project) {
		Network network = project.getNetwork();
		List<TrafficGenerator> generators = project.getTrafficGenerators();

		List<MappedRandomVariable.Entry<DemandGenerator<?>>> subGenerators = new ArrayList<>();

		subGenerators.add(new MappedRandomVariable.Entry<>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new ConstantRandomVariable<>(false),
				new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 210, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new UniformRandomVariable.Generic<>(network.getNodes()),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getGroup("replicas")), new UniformRandomVariable.Generic<>(network.getGroup("replicas")),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(40, 410, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new UniformRandomVariable.Generic<>(network.getGroup("international")),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getGroup("international")), new UniformRandomVariable.Generic<>(network.getNodes()),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));

		generators.add(new TrafficGenerator("No_Backup", new MappedRandomVariable<>(subGenerators)));

		return generators;
	}
}
