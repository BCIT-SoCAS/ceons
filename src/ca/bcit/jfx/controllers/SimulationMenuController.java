package ca.bcit.jfx.controllers;

import ca.bcit.ApplicationResources;
import ca.bcit.io.Logger;
import ca.bcit.jfx.components.TaskReadyProgressBar;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
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
	@FXML private Button PauseButton;
	@FXML private Button CancelButton;
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


//		network.setCanSwitchModulation(allowModulationChange.isSelected());
//		for (Toggle toggle : modulationMetric.getToggles())
//			if (toggle.isSelected())
//				network.setModualtionMetricType(MetricType.valueOf2(((RadioButton) toggle).getText()));

//
//		network.setRegeneratorMetricValue(regeneratorsMetricValue.getValue());
//		for (Toggle toggle : regeneratorsMetric.getToggles())
//			if (toggle.isSelected())
//				network.setRegeneratorMetricType(MetricType.valueOf2(((RadioButton) toggle).getText()));

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
				PauseButton.setText("Pause Simulation");
				finished = true;
			} else {
				paused = false;
				PauseButton.setText("Pause Simulation");
				return;
			}
		} else {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText("Finish simulation");
			alert.setContentText("Are you ok with this?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
				cancelled = true;
				paused = false;
				PauseButton.setText("Pause Simulation");
				finished = true;
			} else {
				paused = false;
				PauseButton.setText("Pause Simulation");
				return;
			}
		}

		settings.setDisable(false);
	}

	// pause button
	public static boolean paused = false;
	@FXML public void pauseSimulation(ActionEvent e) {
		if (paused && !finished) {
			PauseButton.setText("Pause Simulation");
		} else if (!paused && !finished) {
			PauseButton.setText("Resume Simulation");
		} else {
			return;
		}
		paused ^= true; // swap true/false state
	}

	public static boolean updated = false;

	public static void updateSimulation() throws IOException {
		System.out.println("Here");
		FXMLLoader loader = new FXMLLoader(SimulationMenuController.class.getResource("/ca/bcit/jfx/res/MainWindow.fxml"));
		Parent root = (Parent) loader.load();
		MainWindowController mainWindowController = loader.getController();
		mainWindowController.updateGraph();

	}
}
