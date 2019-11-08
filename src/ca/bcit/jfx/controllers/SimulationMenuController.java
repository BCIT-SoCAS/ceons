package ca.bcit.jfx.controllers;

import ca.bcit.ApplicationResources;
import ca.bcit.Settings;
import ca.bcit.io.MapLoadingException;
import ca.bcit.jfx.DrawingState;
import ca.bcit.jfx.components.*;
import ca.bcit.jfx.tasks.SimulationTask;
import ca.bcit.net.MetricType;
import ca.bcit.net.Modulation;
import ca.bcit.net.Network;
import ca.bcit.net.Simulation;
import ca.bcit.net.demand.generator.TrafficGenerator;
import ca.bcit.utils.LocaleUtils;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class SimulationMenuController implements Initializable {
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
	@FXML private VBox settings;
	@FXML private HBox multipleSimulatonSettingsLabel;
	@FXML private HBox multipleSimulatonSettingsRange;
	@FXML private ComboBox<String> algorithms;
	@FXML private ToggleGroup regeneratorsMetric;
	@FXML private ToggleGroup modulationMetric;
	@FXML private CheckBox allowModulationChange;
	@FXML private UIntField bestPaths;
	@FXML private UIntField regeneratorsMetricValue;
	@FXML private Button pauseButton;
	@FXML private Button cancelButton;
	@FXML private Button StartButton;
	@FXML public Label pauseInfoLabel;
	@FXML private Hyperlink algorithmsLink;
	private CheckBox[] modulations;

	/**
	 * To disable and enable Main Controller settings while simulation is running
	 */

	@FXML public void initialize(URL location, ResourceBundle resources) {
		for (Field field : MainWindowController.class.getDeclaredFields()) if (field.isAnnotationPresent(FXML.class))
			try {
				assert field.get(this) != null : "Id '" + field.getName() + "' was not injected!";
			}
			catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}

		algorithms.setItems(new ObservableListWrapper<>(new ArrayList<>(Settings.registeredAlgorithms.keySet())));

		algorithmsLink.setOnMouseClicked(e -> {
			if (algorithms.getValue() == null) {
				Alert selectAlgoAlert = new Alert(Alert.AlertType.ERROR);
				selectAlgoAlert.setHeaderText(LocaleUtils.translate("select_an_algorithm_to_view_the_documentation"));
				selectAlgoAlert.show();
			}
			else {
				String algoKey = algorithms.getValue();
				try {
					Desktop.getDesktop().browse(new URI(Settings.registeredAlgorithms.get(algoKey).getDocumentationURL()));
				}
				catch (IOException | URISyntaxException ex) {
					ex.printStackTrace();
				}
			}
		});

		modulations = new CheckBox[Modulation.values().length];
		for (Modulation modulation : Modulation.values())
			modulations[modulation.ordinal()] = ((CheckBox) settings.lookup("#modulation" + modulation.ordinal()));

		generatorsStatic = generators;
		pauseInfoLabel.managedProperty().bind(pauseInfoLabel.visibleProperty());
	}
	
	void setProgressBar(TaskReadyProgressBar progressBar) {
		SimulationMenuController.progressBar = progressBar;
	}

	@FXML public void multipleSimulationsSelected(ActionEvent e){
		boolean isCheckBoxSelected = runMultipleSimulations.isSelected();
		if(isCheckBoxSelected){

			simulationRepetitions = new CeonsLabel(LocaleUtils.translate("simulation_parameter_simulations_at_each_erlang"), LocaleUtils.translate("simulation_parameter_simulations_at_each_erlang_description"));

			numRepetitionsPerErlang = new UIntField(1);
			numRepetitionsPerErlang.setAlignment(Pos.CENTER);

            erlangRangeLabel = new CeonsLabel(LocaleUtils.translate("simulation_parameter_erlang_range"), LocaleUtils.translate("simulation_parameter_erlang_range_description"));

			stepBetweenErlangsLabel = new CeonsLabel(LocaleUtils.translate("simulation_parameter_step_between_erlangs"), LocaleUtils.translate("simulation_parameter_step_between_erlangs_description"));

			stepBetweenErlangsField = new UIntField(20);
			stepBetweenErlangsField.setAlignment(Pos.CENTER);

			erlangRangeLowLabel = new CeonsLabel(LocaleUtils.translate("simulation_parameter_lower_limit"), LocaleUtils.translate("simulation_parameter_lower_limit_description"));
			erlangRangeLowLabel.setFont(new Font(10));

			erlangRangeLowField = new UIntField(300);
			erlangRangeLowField.setAlignment(Pos.CENTER);

			erlangRangeHighLabel = new CeonsLabel(LocaleUtils.translate("simulation_parameter_higher_limit"), LocaleUtils.translate("simulation_parameter_higher_limit_description"));
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
		}
		else {
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
                alert.setTitle(LocaleUtils.translate("set_routing_algorithm"));
                alert.setHeaderText(null);
                alert.setContentText(LocaleUtils.translate("no_algorithm_selected"));
                alert.setResizable(true);
                alert.getDialogPane().setPrefSize(480.0, 100);
                alert.showAndWait();
                return;
            }
            else if (generators.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(LocaleUtils.translate("set_generators_traffic"));
                alert.setHeaderText(null);
                alert.setContentText(LocaleUtils.translate("traffic_generator_must_be_selected_between_simulations"));
                alert.setResizable(true);
                alert.getDialogPane().setPrefSize(480.0, 100);
                alert.showAndWait();
                return;
            }
			else if (bestPaths.getValue() > network.getMaxPathsCount() || bestPaths.getValue() <= 0){
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Set Number of Candidate Paths");
				alert.setHeaderText(null);
				if(bestPaths.getValue() > network.getMaxPathsCount()){
					alert.setContentText("Number of candidate paths must be less than best paths count");
//					bestPaths.replaceText(0, bestPaths.getText().length(), String.valueOf(network.getMaxPathsCount()));
				} else {
					alert.setContentText("Number of candidate paths can't be 0 or negative");
				}
				bestPaths.setStyle("-fx-border-color: red; -fx-border-width: 1; -fx-border-radius: 2");
				bestPaths.setOnKeyTyped(event -> {
					bestPaths.setStyle("-fx-border-width: 0;");
				});
				alert.setResizable(true);
				alert.getDialogPane().setPrefSize(480.0, 100);
				alert.showAndWait();
				return;
            }
            else if (demands.getValue() <= 0){
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle(LocaleUtils.translate("set_number_of_requests"));
				alert.setHeaderText(null);
				alert.setContentText(LocaleUtils.translate("number_of_requests_must_be_greater_than_zero"));
				alert.setResizable(true);
				alert.getDialogPane().setPrefSize(480.0, 100);
				alert.showAndWait();
				return;
			}

            network.setDemandAllocationAlgorithm(Settings.registeredAlgorithms.get(algorithms.getValue()));

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
                SimulationTask task = new SimulationTask(simulation, seedField.getValue(), Double.parseDouble(alpha.getText()), erlangIntField.getValue(), demands.getValue(), true, this);
                progressBar.runTask(task, true);
            } else {
            	if(erlangRangeLowField.getValue() > erlangRangeHighField.getValue() || erlangRangeLowField.getValue() == erlangRangeHighField.getValue()){
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle(LocaleUtils.translate("erlang_range"));
					alert.setHeaderText(null);
					alert.setContentText(LocaleUtils.translate("lower_erlang_range_must_be_less_than_upper_erlang_range"));
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
				ArrayList<ArrayList> tasks = new ArrayList<>();
				for(int numRepetitions = 1; numRepetitions <= numRepetitionsPerErlang.getValue(); numRepetitions++){
					int randomSeed = random.nextInt(101);

                    TaskReadyProgressBar.addResultsDataSeed(randomSeed);
					for (int erlangValue = erlangRangeLowField.getValue(); erlangValue <= erlangRangeHighField.getValue(); erlangValue+=stepBetweenErlangsField.getValue()){
						simulation = new Simulation(network, generators.getValue());
						simulation.setMultipleSimulations(true);
						ArrayList taskSettingsArray = new ArrayList();
						taskSettingsArray.add(simulation);
						taskSettingsArray.add(randomSeed);
						taskSettingsArray.add(Double.parseDouble(alpha.getText()));
						taskSettingsArray.add(erlangValue);
						taskSettingsArray.add(demands.getValue());
						taskSettingsArray.add(true);
						tasks.add(taskSettingsArray);
					}
				}
				progressBar.runTasks(tasks, true, runMultipleSimulationService, this);
            }
        }
	    catch (NullPointerException ex) {
	    	ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(LocaleUtils.translate("starting_simulation"));
            alert.setHeaderText(null);
            alert.setContentText(LocaleUtils.translate("load_a_project_into_the_simulator"));
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(480.0, 100);
            alert.showAndWait();
            setRunning(false);
        }
	}
	
	public void setRunning(boolean isRunning){
		for (Node node: new Node[] {generators,
		runMultipleSimulations, simulationRepetitions, erlangLabel, stepBetweenErlangsLabel, erlangRangeLabel,
		erlangRangeLowLabel, erlangRangeHighLabel, seedLabel, erlangIntField, erlangRangeLowField, numRepetitionsPerErlang,
		stepBetweenErlangsField, erlangRangeHighField, seedField, alpha, demands,  settings,
		multipleSimulatonSettingsLabel, multipleSimulatonSettingsRange, algorithms, allowModulationChange, bestPaths,
		regeneratorsMetricValue
		}) {
			try {
				node.setDisable(isRunning);
			}
			catch (NullPointerException ignored) {}
		}
		pauseButton.setDisable(!isRunning);
		pauseInfoLabel.setVisible(isRunning);
		cancelButton.setDisable(!isRunning);
		StartButton.setDisable(isRunning);
	}

	// Cancel simulation button
	@FXML public void cancelSimulation(ActionEvent e) {
		paused = true;

		if (!finished) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle(LocaleUtils.translate("confirmation"));
			alert.setHeaderText(LocaleUtils.translate("cancel_current_simulation"));
			alert.setContentText(LocaleUtils.translate("cancel_current_simulation_confirmation_question"));

			Optional<ButtonType> result = alert.showAndWait();
			//User clicks OK
			if (result.get() == ButtonType.OK){
				cancelled = true;
				paused = false;
				pauseButton.setText(LocaleUtils.translate("pause_icon"));
				pauseButton.setTooltip(new Tooltip(LocaleUtils.translate("pause_simulation")));
				finished = true;
				started = false;
				TaskReadyProgressBar.getResultsDataFileNameList().clear();
				TaskReadyProgressBar.getResultsDataSeedList().clear();
				ResizableCanvas.getParentController().resetGraph();
				ResizableCanvas.getParentController().graph.changeState(DrawingState.noActionState);
				if (runMultipleSimulations.isSelected()) {
					try {
						progressBar.getRunMultipleSimulationService().shutdownNow();
						ResizableCanvas.getParentController().initalizeSimulationsAndNetworks();
					}
					catch (MapLoadingException ex) {
						new ErrorDialog(ex.getMessage(), ex);
						ex.printStackTrace();
						return;
					}
					catch (Exception ex) {
						new ErrorDialog(LocaleUtils.translate("an_exception_occurred_label"), ex);
						ex.printStackTrace();
						return;
					}
				}
			//User cancels cancel
			}
			else {
				paused = false;
				pauseButton.setText(LocaleUtils.translate("pause_icon"));
				pauseButton.setTooltip(new Tooltip(LocaleUtils.translate("pause_simulation")));
				return;
			}
		}

		settings.setDisable(false);
	}

	@FXML public void pauseSimulation(ActionEvent e) {
		if (paused && !finished && started) {
			ResizableCanvas.getParentController().graph.changeState(DrawingState.noActionState);
			pauseButton.setText(LocaleUtils.translate("pause_icon"));
			pauseButton.setTooltip(new Tooltip(LocaleUtils.translate("pause_simulation")));
		}
		else if (!paused && !finished && started) {
			ResizableCanvas.getParentController().setExpandedPane(2);
			ResizableCanvas.getParentController().whilePaused();
			pauseButton.setText(LocaleUtils.translate("resume_icon"));
			pauseButton.setTooltip(new Tooltip(LocaleUtils.translate("resume_simulation")));
		}
		else
			return;

		paused ^= true;
	}

	@FXML public void clear(ActionEvent e) {
		ResizableCanvas.getParentController().graph.resetCanvas();
	}

	@FXML public void disableClearSimulationButton(){
		cancelButton.setDisable(true);
	}
}
