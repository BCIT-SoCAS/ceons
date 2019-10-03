package ca.bcit.jfx.controllers;

import ca.bcit.ApplicationResources;
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
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class SimulationMenuController {

    public static boolean started = false;
    public static boolean finished = false;
    public static boolean paused = false;
    public static boolean cancelled = false;

    public static TaskReadyProgressBar progressBar;
    public static ComboBox<TrafficGenerator> generatorsStatic;

    private static final int SIMULATION_REPETITION_LABEL_INDEX = 7;
    private static final int ERLANG_RANGE_LABEL_INDEX = 9;
    private static final int ERLANG_LABEL_INDEX = 8;
    private static final int ERLANG_INT_FIELD_INDEX = 9;
	
	@FXML private ComboBox<TrafficGenerator> generators;
	@FXML private CheckBox runMultipleSimulations;
	@FXML private Label simulationRepetitions;
	@FXML private Label erlangLabel;
	@FXML private Label stepBetweenErlangsLabel;
	@FXML private Label erlangRangeLabel;
	@FXML private Label erlangRangeLowLabel;
	@FXML private Label erlangRangeHighLabel;
	@FXML private Label seedLabel;
	@FXML private UIntField erlangIntField;
	@FXML private UIntField erlangRangeLowField;
	@FXML private UIntField numRepetitionsPerErlang;
	@FXML private UIntField stepBetweenErlangsField;
	@FXML private UIntField erlangRangeHighField;
	@FXML private UIntField seedField;
	@FXML private TextField alpha;
	@FXML private UIntField demands;
	@FXML private CheckBox replicaPreservation;
	@FXML private VBox settings;
	@FXML private HBox multipleSimulatonSettingsLabel;
	@FXML private HBox multipleSimulatonSettingsRange;
	@FXML private ComboBox<RMSAAlgorithm> algorithms;
	@FXML private ToggleGroup regeneratorsMetric;
	@FXML private ToggleGroup modulationMetric;
	@FXML private CheckBox allowModulationChange;
	@FXML private UIntField bestPaths;
	@FXML private UIntField regeneratorsMetricValue;
	@FXML private Button pauseButton;
	@FXML private Button cancelButton;
	private CheckBox[] modulations;

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

	@FXML public void multipleSimulationsSelected(ActionEvent e){
		boolean isCheckBoxSelected = runMultipleSimulations.isSelected();
		if(isCheckBoxSelected){
			simulationRepetitions = new Label("Simulations at each Erlang");
			simulationRepetitions.setStyle("-fx-font-weight: bold;");

			numRepetitionsPerErlang = new UIntField(1);
			numRepetitionsPerErlang.setAlignment(Pos.CENTER);

            erlangRangeLabel = new Label("Erlang Range");
			erlangRangeLabel.setStyle("-fx-font-weight: bold;");

			stepBetweenErlangsLabel = new Label("Step between Erlangs");
			stepBetweenErlangsLabel.setStyle("-fx-font-weight: bold;");

			stepBetweenErlangsField = new UIntField(20);
			stepBetweenErlangsField.setAlignment(Pos.CENTER);

			erlangRangeLowLabel = new Label("Lower limit");
			erlangRangeLowLabel.setFont(new Font(10));

			erlangRangeLowField = new UIntField(300);
			erlangRangeLowField.setAlignment(Pos.CENTER);

			erlangRangeHighLabel = new Label("Higher limit");
			erlangRangeHighLabel.setFont(new Font(10));

			erlangRangeHighField = new UIntField(700);
			erlangRangeHighField.setAlignment(Pos.CENTER);

			settings.getChildren().remove(erlangLabel);
			settings.getChildren().remove(erlangIntField);
			settings.getChildren().remove(seedLabel);
			settings.getChildren().remove(seedField);

			settings.getChildren().add(SIMULATION_REPETITION_LABEL_INDEX, simulationRepetitions);
			settings.getChildren().add(SIMULATION_REPETITION_LABEL_INDEX + 1, numRepetitionsPerErlang);

			settings.getChildren().add(ERLANG_RANGE_LABEL_INDEX, erlangRangeLabel);
			multipleSimulatonSettingsLabel.getChildren().add(erlangRangeLowLabel);
			multipleSimulatonSettingsLabel.getChildren().add(erlangRangeHighLabel);
			multipleSimulatonSettingsRange.getChildren().add(erlangRangeLowField);
			multipleSimulatonSettingsRange.getChildren().add(erlangRangeHighField);

			settings.getChildren().add(ERLANG_RANGE_LABEL_INDEX + 3, stepBetweenErlangsLabel);
			settings.getChildren().add(ERLANG_RANGE_LABEL_INDEX + 4, stepBetweenErlangsField);
		} else {

            settings.getChildren().remove(simulationRepetitions);
			settings.getChildren().remove(numRepetitionsPerErlang);
            settings.getChildren().remove(erlangRangeLabel);
            settings.getChildren().remove(stepBetweenErlangsLabel);
            settings.getChildren().remove(stepBetweenErlangsField);
			multipleSimulatonSettingsLabel.getChildren().clear();
			multipleSimulatonSettingsRange.getChildren().clear();

			settings.getChildren().add(ERLANG_LABEL_INDEX, erlangLabel);
			settings.getChildren().add(ERLANG_INT_FIELD_INDEX, erlangIntField);
			settings.getChildren().add(ERLANG_INT_FIELD_INDEX + 1, seedLabel);
			settings.getChildren().add(ERLANG_INT_FIELD_INDEX + 2, seedField);
		}

		erlangLabel.setVisible(!erlangLabel.isVisible());
		erlangIntField.setVisible(!erlangIntField.isVisible());
	}

	// start simulation button
	@FXML public void startSimulation(ActionEvent e) {
		Simulation simulation;

	    try {
            Network network = ApplicationResources.getProject().getNetwork();

            // Initial checks
            if (algorithms.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Set Routing Algorithm");
                alert.setHeaderText(null);
                alert.setContentText("No algorithm selected!");
                alert.setResizable(true);
                alert.getDialogPane().setPrefSize(480.0, 100);
                alert.showAndWait();
                return;
            } else if (generators.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Set Generators Traffic");
                alert.setHeaderText(null);
                alert.setContentText("Traffic generator must be selected between simulations!");
                alert.setResizable(true);
                alert.getDialogPane().setPrefSize(480.0, 100);
                alert.showAndWait();
                return;
            } else if (bestPaths.getValue() > network.getMaxPathsCount() || bestPaths.getValue() <= 0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Set Number of Candidate Paths");
                alert.setHeaderText(null);
                if(bestPaths.getValue() > network.getMaxPathsCount()){
					alert.setContentText("Number of candidate paths must be less than best paths count");
				} else {
					alert.setContentText("Number of candidate paths can't be 0 or negative");
				}
                alert.setResizable(true);
                alert.getDialogPane().setPrefSize(480.0, 100);
                alert.showAndWait();
                return;
            } else if (demands.getValue() <= 0){
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Set Demands Counts");
				alert.setHeaderText(null);
				alert.setContentText("Demands count can't be less than or equal to 0");
				alert.setResizable(true);
				alert.getDialogPane().setPrefSize(480.0, 100);
				alert.showAndWait();
				return;
			}

            network.setDemandAllocationAlgorithm(algorithms.getValue());

            //Initially remove all modulations first and add back modulations that user selects
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

            cancelButton.setDisable(false);

            //If multiple simulations is selected then we will create a single thread executor otherwise to run multiple consecutive simulations back-to-back, otherwise, run one task
            if(!runMultipleSimulations.isSelected()){
                simulation = new Simulation(network, generators.getValue());

                //TODO: REFACTOR SIMULATION TASK INTO SIMULATION
                SimulationTask task = new SimulationTask(simulation, seedField.getValue(), Double.parseDouble(alpha.getText()), erlangIntField.getValue(), demands.getValue(), replicaPreservation.isSelected());
                progressBar.runTask(task, true);
            } else {
            	if(erlangRangeLowField.getValue() > erlangRangeHighField.getValue() || erlangRangeLowField.getValue() == erlangRangeHighField.getValue()){
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Erlang Range");
					alert.setHeaderText(null);
					alert.setContentText("Lower erlang range can't be equal or greater than upper erlang range");
					alert.setResizable(true);
					alert.getDialogPane().setPrefSize(480.0, 100);
					alert.showAndWait();
					return;
				}
				final ExecutorService runMultipleSimulationService = Executors.newSingleThreadExecutor(new ThreadFactory() {
					@Override
					public Thread newThread(Runnable runnable) {
						Thread thread = Executors.defaultThreadFactory().newThread(runnable);
						thread.setDaemon(true);
						return thread;
					}
				});
                Random random = new Random();

				for(int numRepetitions = 1; numRepetitions <= numRepetitionsPerErlang.getValue(); numRepetitions++){
					int randomSeed = random.nextInt(101);
                    TaskReadyProgressBar.addResultsDataSeed(randomSeed);
					for(int erlangValue = erlangRangeLowField.getValue(); erlangValue <= erlangRangeHighField.getValue(); erlangValue+=stepBetweenErlangsField.getValue()){
						simulation = new Simulation(network, generators.getValue());
						simulation.setMultipleSimulations(true);
						SimulationTask simulationTask = new SimulationTask(simulation, randomSeed, Double.parseDouble(alpha.getText()), erlangValue, demands.getValue(), replicaPreservation.isSelected());
						progressBar.runTask(simulationTask, true, runMultipleSimulationService);
						progressBar.increaseSimulationCount();
					}
				}
            }
        } catch (NullPointerException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Starting Simulation");
            alert.setHeaderText(null);
            alert.setContentText("Load a project into the simulator!");
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(480.0, 100);
            alert.showAndWait();
        }
	}

	// Cancel simulation button
	@FXML public void cancelSimulation(ActionEvent e) {

		paused = true;

		if (!finished){
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText("Cancel current simulation");
			alert.setContentText("Are you ok with this?");

			Optional<ButtonType> result = alert.showAndWait();
			//User clicks OK
			if (result.get() == ButtonType.OK){
				cancelled = true;
				paused = false;
				pauseButton.setText("Pause Simulation");
				finished = true;
				started = false;
				TaskReadyProgressBar.getResultsDataFileNameList().clear();
				TaskReadyProgressBar.getResultsDataSeedList().clear();
				ResizableCanvas.getParentController().resetGraph();
				ResizableCanvas.getParentController().graph.changeState(DrawingState.noActionState);
				if(runMultipleSimulations.isSelected()){
					try {
						progressBar.getRunMultipleSimulationService().shutdownNow();
						ResizableCanvas.getParentController().initalizeSimulationsAndNetworks();
					} catch (MapLoadingException ex){
						new ErrorDialog(ex.getMessage(), ex);
						ex.printStackTrace();
						return;
					} catch (Exception ex){
						new ErrorDialog("An exception occurred: ", ex);
						ex.printStackTrace();
						return;
					}
				}
			//User cancels cancel
			} else {
				paused = false;
				pauseButton.setText("Pause Simulation");
				return;
			}
		}

		settings.setDisable(false);
	}

	// pause simulation button
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

	// clear canvas button
	@FXML public void clear(ActionEvent e) {
		ResizableCanvas.getParentController().graph.resetCanvas();
	}


	@FXML public void disableClearSimulationButton(){
		cancelButton.setDisable(true);
	}
}
