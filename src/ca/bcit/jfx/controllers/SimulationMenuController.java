package ca.bcit.jfx.controllers;

import ca.bcit.ApplicationResources;
import ca.bcit.io.Logger;
import ca.bcit.jfx.DrawingState;
import ca.bcit.jfx.components.TaskReadyProgressBar;
import ca.bcit.jfx.components.ResizableCanvas;
import ca.bcit.jfx.components.UIntField;
import ca.bcit.jfx.tasks.SimulationTask;
import ca.bcit.net.MetricType;
import ca.bcit.net.Modulation;
import ca.bcit.net.Network;
import ca.bcit.net.Simulation;
import ca.bcit.net.algo.RMSAAlgorithm;
import ca.bcit.net.demand.generator.TrafficGenerator;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Optional;

public class SimulationMenuController {
	
	public static ComboBox<TrafficGenerator> generatorsStatic;
	
	@FXML private ComboBox<TrafficGenerator> generators;
	@FXML private UIntField erlang;
	@FXML private UIntField seed;
	@FXML private TextField alpha;
	@FXML private UIntField demands;
	@FXML private CheckBox replicaPreservation;
	@FXML private VBox settings;
	@FXML private ComboBox<RMSAAlgorithm> algorithms;
	@FXML private ToggleGroup regeneratorsMetric;
	@FXML private ToggleGroup modulationMetric;
	@FXML private CheckBox allowModulationChange;
	@FXML private UIntField bestPaths;
	@FXML private UIntField regeneratorsMetricValue;
	@FXML private Button pauseButton;
	@FXML private Button cancelButton;
	private CheckBox[] modulations;

	public static TaskReadyProgressBar progressBar;

	/**
	 * To disable and enable Main Controller settings while simulation is running
	 */

	@FXML public void initialize() {
		for (Field field : MainWindowController.class.getDeclaredFields()) if (field.isAnnotationPresent(FXML.class))
			try {
				assert field.get(this) != null : "Id '" + field.getName() + "' was not injected!";
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		algorithms.setItems(new ObservableListWrapper<>(new ArrayList<>(RMSAAlgorithm.getRegisteredAlgorithms())));
		
		modulations = new CheckBox[Modulation.values().length];
		for (Modulation modulation : Modulation.values())
			modulations[modulation.ordinal()] = ((CheckBox) settings.lookup("#modulation" + modulation.ordinal()));
		
		generatorsStatic = generators;
	}
	
	void setProgressBar(TaskReadyProgressBar progressBar) {
		SimulationMenuController.progressBar = progressBar;
	}

	public static boolean started = false;
	public static boolean finished = false;
	@FXML public void startSimulationAction(ActionEvent e) {
		Network network = ApplicationResources.getProject().getNetwork();

		if (algorithms.getValue() == null) {
			Logger.info("No algorithm selected!");
			return;
		}

		network.setDemandAllocationAlgorithm(algorithms.getValue());
		for (Modulation modulation : network.getAllowedModulations()) network.disallowModulation(modulation);
		for (Modulation modulation : Modulation.values())
			if (modulations[modulation.ordinal()].isSelected())
				network.allowModulation(modulation);
		network.setBestPathsCount(bestPaths.getValue());

		//Modulation Metric is always dynamic
		network.setModualtionMetricType(MetricType.DYNAMIC);
		//Regenerator Metric value is always set to 5
		network.setRegeneratorMetricValue(5);
		//Regenerator Metric is always static
		network.setRegeneratorMetricType(MetricType.STATIC);

		Simulation simulation = new Simulation(network, generators.getValue());
		SimulationTask task = new SimulationTask(simulation, seed.getValue(), Double.parseDouble(alpha.getText()), erlang.getValue(), demands.getValue(), replicaPreservation.isSelected());

		//gray out settings
		settings.setDisable(true);
		progressBar.runTask(task, true);
	}

	public static boolean cancelled = false;
	@FXML public void cancelSimulation(ActionEvent e) {

		paused = true;

		if (!finished){
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText("Cancel current simulation");
			alert.setContentText("Are you ok with this?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
				cancelled = true;
				paused = false;
				pauseButton.setText("Pause Simulation");
				finished = true;
				started = false;
				ResizableCanvas.getParentController().resetGraph();
				ResizableCanvas.getParentController().graph.changeState(DrawingState.noActionState);
			} else {
				paused = false;
				pauseButton.setText("Pause Simulation");
				return;
			}
		} else {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText("Reset simulation");
			alert.setContentText("Are you ok with this?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
				cancelled = true;
				paused = false;
				pauseButton.setText("Pause Simulation");
				finished = true;
				started = false;
				ResizableCanvas.getParentController().resetGraph();
			} else {
				paused = false;
				pauseButton.setText("Pause Simulation");
				return;
			}
		}

		settings.setDisable(false);
	}

	// pause button
	public static boolean paused = false;
	@FXML public void pauseSimulation(ActionEvent e) {
		if (paused && !finished && started) {
			ResizableCanvas.getParentController().graph.changeState(DrawingState.noActionState);
			pauseButton.setText("Pause Simulation");
		} else if (!paused && !finished && started) {
			ResizableCanvas.getParentController().setExpandedPane(2);
			ResizableCanvas.getParentController().whilePaused();
			pauseButton.setText("Resume Simulation");
		} else {
			return;
		}
		paused ^= true; // swap true/false state
	}

}
