package ca.bcit.jfx.controllers;

import ca.bcit.ApplicationResources;
import ca.bcit.io.Logger;
import ca.bcit.io.MapLoadingException;
import ca.bcit.jfx.DrawingState;
import ca.bcit.jfx.components.ErrorDialog;
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

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Optional;

public class SimulationMenuController {

	public static ComboBox<TrafficGenerator> generatorsStatic;
	
	@FXML private ComboBox<TrafficGenerator> generators;
	@FXML private CheckBox runMultipleSimulations;
	@FXML private Label erlangLabel;
	@FXML private UIntField erlangIntField;
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
	@FXML private Button clearCancelButton;
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

	@FXML public void runMultipleSimulations(ActionEvent e){
		boolean isCheckBoxSelected = runMultipleSimulations.isSelected();
		if(isCheckBoxSelected){
			settings.getChildren().remove(erlangLabel);
			settings.getChildren().remove(erlangIntField);
//			RangeSlider r = new RangeSlider();
		}

		erlangLabel.setVisible(!erlangLabel.isVisible());
		erlangIntField.setVisible(!erlangIntField.isVisible());
	}

	// start simulation button
	public static boolean started = false;
	public static boolean finished = false;
	@FXML public void startSimulation(ActionEvent e) {
		Network network = ApplicationResources.getProject().getNetwork();

		if (algorithms.getValue() == null) {
			Logger.info("No algorithm selected!");
			return;
		} else if (generators.getValue() == null) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Set Generators Traffic");
			alert.setHeaderText(null);
			alert.setContentText("Traffic generator must be selected between simulations!");
			alert.setResizable(true);
			alert.getDialogPane().setPrefSize(480.0, 100);
			alert.showAndWait();
			return;
		}

		network.setDemandAllocationAlgorithm(algorithms.getValue());
		network.setTrafficGenerator(generators.getValue());
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
		SimulationTask task = new SimulationTask(simulation, seed.getValue(), Double.parseDouble(alpha.getText()), erlangIntField.getValue(), demands.getValue(), replicaPreservation.isSelected());
		//gray out settings
		clearCancelButton.setText("Cancel Simulation");
		settings.setDisable(true);
		progressBar.runTask(task, true);
	}

	public void isFinished() {
		clearCancelButton.setText("Clear Simulation");
	}

	// Cancel simulation button
	public static boolean cancelled = false;
	@FXML public void clearCancelSimulation(ActionEvent e) {

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
				clearCancelButton.setText("Clear Simulation");
				pauseButton.setText("Pause Simulation");
				finished = true;
				started = false;
				ResizableCanvas.getParentController().resetGraph();
				ResizableCanvas.getParentController().graph.changeState(DrawingState.noActionState);
				try {
					ResizableCanvas.getParentController().initalizeSimulationsAndNetworks();
				} catch (MapLoadingException ex){
					new ErrorDialog(ex.getMessage(), ex);
					ex.printStackTrace();
					return;
				} catch (Exception ex){
					new ErrorDialog("An exception occurred while loading the project.", ex);
					ex.printStackTrace();
					return;
				}
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
				clearCancelButton.setText("Clear Simulation");
				pauseButton.setText("Pause Simulation");
				finished = true;
				started = false;
				ResizableCanvas.getParentController().resetGraph();
				try {
					ResizableCanvas.getParentController().initalizeSimulationsAndNetworks();
				} catch (MapLoadingException ex){
					new ErrorDialog(ex.getMessage(), ex);
					ex.printStackTrace();
					return;
				} catch (Exception ex){
					new ErrorDialog("An exception occurred while loading the project.", ex);
					ex.printStackTrace();
					return;
				}
			} else {
				paused = false;
				pauseButton.setText("Pause Simulation");
				return;
			}
		}

		settings.setDisable(false);
	}

	// pause simulation button
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
