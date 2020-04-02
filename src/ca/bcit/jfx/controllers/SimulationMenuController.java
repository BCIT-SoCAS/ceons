package ca.bcit.jfx.controllers;

import ca.bcit.ApplicationResources;
import ca.bcit.Settings;
import ca.bcit.io.MapLoadingException;
import ca.bcit.io.project.Project;
import ca.bcit.jfx.DrawingState;
import ca.bcit.jfx.components.*;
import ca.bcit.jfx.tasks.SimulationTask;
import ca.bcit.net.MetricType;
import ca.bcit.net.Network;
import ca.bcit.net.Simulation;
import ca.bcit.net.algo.IRMSAAlgorithm;
import ca.bcit.net.demand.generator.TrafficGenerator;
import ca.bcit.net.modulation.IModulation;
import ca.bcit.utils.LocaleUtils;
import ca.bcit.utils.random.PasswordEncrypter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class SimulationMenuController implements Initializable {
	public FlowPane algoCheckBoxContainer;

	public static boolean started = false;
	public static boolean finished = false;

	public static boolean paused = false;
	public static boolean cancelled = false;

	public static TaskReadyProgressBar progressBar;
	public static ComboBox<TrafficGenerator> generatorsStatic;

	private static final int SIMULATION_REPETITION_LABEL_INDEX = 7;
	private static final int ERLANG_RANGE_LABEL_INDEX = 10;
	private static final int ERLANG_LABEL_INDEX = 8;
	private static final int ERLANG_INT_FIELD_INDEX = 9;
	public CheckBox emailCheckbox;
	public TextField emailInput;

	@FXML
	private ComboBox<TrafficGenerator> generators;
	@FXML
	private CheckBox runMultipleSimulations;
	@FXML
	private Label simulationRepetitions;
	@FXML
	private Label erlangLabel;
	@FXML
	private Label stepBetweenErlangsLabel;
	@FXML
	private Label erlangRangeLabel;
	@FXML
	private Label erlangRangeLowLabel;
	@FXML
	private Label erlangRangeHighLabel;
	@FXML
	private Label seedLabel;
	@FXML
	private UIntField erlangIntField;
	@FXML
	private UIntField erlangRangeLowField;
	@FXML
	private UIntField numRepetitionsPerErlang;
	@FXML
	private UIntField stepBetweenErlangsField;
	@FXML
	private UIntField erlangRangeHighField;
	@FXML
	private UIntField seedField;
	@FXML
	private TextField alpha;
	@FXML
	private UIntField demands;
	@FXML
	private VBox settings;

	@FXML
	private VBox singleSimulationSettingsErlang;
	@FXML
	private VBox singleSimulationSettingsSeed;

	@FXML
	private HBox multipleSimulatonSettingsLabel;
	@FXML
	private HBox multipleSimulatonSettingsRange;

	@FXML
	private VBox multipleSimulationSettingsErlangLowerLimit;
	@FXML
	private VBox multipleSimulationSettingsErlangUpperLimit;
	@FXML
	private VBox multipleSimulationSettingsYearLowerLimit;
	@FXML
	private VBox multipleSimulationSettingsYearUpperLimit;
	@FXML
	private VBox simulationsAtEachErlang;
	@FXML
	private VBox stepsBetweenErlangs;

	@FXML
	private ComboBox<String> trafficDataYearSelection;

	@FXML
	private Label yearRangeLowLabel;
	@FXML
	private Label yearRangeHighLabel;
	@FXML
	private ComboBox<String> yearRangeLowField;
	@FXML
	private ComboBox<String> yearRangeHighField;

	@FXML
	private ComboBox<String> algorithms;
	@FXML
	private ToggleGroup regeneratorsMetric;
	@FXML
	private ToggleGroup modulationMetric;
	@FXML
	private CheckBox allowModulationChange;
	@FXML
	private UIntField bestPaths;
	@FXML
	private UIntField regeneratorsMetricValue;
	@FXML
	private Button pauseButton;
	@FXML
	private Button cancelButton;
	@FXML
	private Button StartButton;
	@FXML
	public Label pauseInfoLabel;
	@FXML
	private Hyperlink algorithmsLink;
	private CheckBox[] modulations;
	private boolean multipleSimulationsRan = false;

	/**
	 * To disable and enable Main Controller settings while simulation is running
	 */

	@FXML
	public void initialize(URL location, ResourceBundle resources) {
		for (Field field : MainWindowController.class.getDeclaredFields())
			if (field.isAnnotationPresent(FXML.class))
				try {
					assert field.get(this) != null : "Id '" + field.getName() + "' was not injected!";
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}

		algorithmsLink.setOnMouseClicked(e -> {
			if (algorithms.getValue() == null) {
				Alert selectAlgoAlert = new Alert(Alert.AlertType.ERROR);
				selectAlgoAlert.setHeaderText(LocaleUtils.translate("select_an_algorithm_to_view_the_documentation"));
				selectAlgoAlert.show();
			}
			else {
				String algoKey = "";
				for (IRMSAAlgorithm algorithm : Settings.registeredAlgorithms.values())
					if (algorithm.getName().equals(algorithms.getValue()))
						algoKey = algorithm.getKey();
				try {
					Desktop.getDesktop().browse(new URI(Settings.registeredAlgorithms.get(algoKey).getDocumentationURL()));
				}
				catch (IOException | URISyntaxException ex) {
					ex.printStackTrace();
				}
			}
		});

		yearRangeLowLabel = new CeonsLabel(LocaleUtils.translate("year_lower_range"));
		yearRangeLowLabel.setFont(new Font(10));

		yearRangeLowField.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");
		yearRangeLowField.setValue(Integer.toString(Settings.DEFAULT_YEAR));

		yearRangeHighLabel = new CeonsLabel(LocaleUtils.translate("year_upper_range"));
		yearRangeHighLabel.setFont(new Font(10));

		yearRangeHighField.getItems().addAll("2019", "2020", "2021", "2022", "2023");
		yearRangeHighField.setValue(Integer.toString(Settings.DEFAULT_YEAR + 1));

		modulations = new CheckBox[Settings.registeredModulations.size()];
		for (IModulation modulation : Settings.registeredModulations.values())
			modulations[modulation.getId()] = ((CheckBox) settings.lookup("#modulation" + modulation.getId()));

		generatorsStatic = generators;
		pauseButton.managedProperty().bind(pauseButton.visibleProperty());
		StartButton.managedProperty().bind(StartButton.visibleProperty());
		cancelButton.managedProperty().bind(cancelButton.visibleProperty());
		pauseInfoLabel.managedProperty().bind(pauseInfoLabel.visibleProperty());
		for (IRMSAAlgorithm algo : Settings.registeredAlgorithms.values()) {
			CheckBox checkBox = new CheckBox(algo.getName());
			checkBox.setStyle("-fx-padding: 3;");
			algorithms.getItems().add(algo.getName());
			algoCheckBoxContainer.getChildren().add(checkBox);
		}
		algoCheckBoxContainer.managedProperty().bind(algoCheckBoxContainer.visibleProperty());
		algorithms.managedProperty().bind(algorithms.visibleProperty());
		algorithms.visibleProperty().bind(runMultipleSimulations.selectedProperty().not());
		algorithmsLink.managedProperty().bind(algorithmsLink.visibleProperty());
		algorithmsLink.visibleProperty().bind(runMultipleSimulations.selectedProperty().not());
		algoCheckBoxContainer.visibleProperty().bind(runMultipleSimulations.selectedProperty());
		emailInput.visibleProperty().bind(emailCheckbox.selectedProperty());
		emailInput.managedProperty().bind(emailCheckbox.selectedProperty());
		trafficDataYearSelection.getItems().addAll(LocaleUtils.translate("custom"), "2018", "2019", "2020", "2021", "2022", "2023");
		trafficDataYearSelection.setValue(LocaleUtils.translate("custom"));
		multipleSimulationSettingsYearLowerLimit.getChildren().clear();
		multipleSimulationSettingsYearUpperLimit.getChildren().clear();
	}

	void setProgressBar(TaskReadyProgressBar progressBar) {
		SimulationMenuController.progressBar = progressBar;
	}

	@FXML
	public void trafficDataIndexChanged(ActionEvent event) {
		String trafficDataVal = trafficDataYearSelection.getValue();

		boolean isMultipleSimulationsSelected = runMultipleSimulations.isSelected();
		if (isMultipleSimulationsSelected) {
			multipleSimulationSettingsYearLowerLimit.getChildren().clear();
			multipleSimulationSettingsYearUpperLimit.getChildren().clear();
			multipleSimulationSettingsErlangLowerLimit.getChildren().clear();
			multipleSimulationSettingsErlangUpperLimit.getChildren().clear();
			if (!trafficDataVal.equals(LocaleUtils.translate("custom"))) {
				multipleSimulationSettingsYearLowerLimit.getChildren().add(yearRangeLowLabel);
				multipleSimulationSettingsYearLowerLimit.getChildren().add(yearRangeLowField);
				multipleSimulationSettingsYearUpperLimit.getChildren().add(yearRangeHighLabel);
				multipleSimulationSettingsYearUpperLimit.getChildren().add(yearRangeHighField);
			}
			else {
				multipleSimulationSettingsErlangLowerLimit.getChildren().add(erlangRangeLowLabel);
				multipleSimulationSettingsErlangLowerLimit.getChildren().add(erlangRangeLowField);
				multipleSimulationSettingsErlangUpperLimit.getChildren().add(erlangRangeHighLabel);
				multipleSimulationSettingsErlangUpperLimit.getChildren().add(erlangRangeHighField);
			}
		}
		else {
			if (!trafficDataVal.equals(LocaleUtils.translate("custom")))
				erlangIntField.setDisable(true);
			else
				erlangIntField.setDisable(false);
		}
	}

	@FXML
	public void multipleSimulationsSelected(ActionEvent e) {
		boolean isCheckBoxSelected = runMultipleSimulations.isSelected();
		if (isCheckBoxSelected) {
			trafficDataYearSelection.getItems().removeAll("2018", "2019", "2020", "2021", "2022", "2023");
			trafficDataYearSelection.getItems().addAll("Year Selection");

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

			singleSimulationSettingsErlang.getChildren().remove(erlangLabel);
			singleSimulationSettingsErlang.getChildren().remove(erlangIntField);
			singleSimulationSettingsSeed.getChildren().remove(seedLabel);
			singleSimulationSettingsSeed.getChildren().remove(seedField);

			simulationsAtEachErlang.getChildren().add(simulationRepetitions);
			simulationsAtEachErlang.getChildren().add(numRepetitionsPerErlang);

			/*settings.getChildren().add(ERLANG_RANGE_LABEL_INDEX, erlangRangeLabel);*/
			multipleSimulationSettingsErlangLowerLimit.getChildren().add(erlangRangeLowLabel);
			multipleSimulationSettingsErlangUpperLimit.getChildren().add(erlangRangeHighLabel);
			multipleSimulationSettingsErlangLowerLimit.getChildren().add(erlangRangeLowField);
			multipleSimulationSettingsErlangUpperLimit.getChildren().add(erlangRangeHighField);

			stepsBetweenErlangs.getChildren().add(stepBetweenErlangsLabel);
			stepsBetweenErlangs.getChildren().add(stepBetweenErlangsField);
		}
		else {
			trafficDataYearSelection.getItems().removeAll(LocaleUtils.translate("year_selection"));
			trafficDataYearSelection.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");
			multipleSimulationSettingsYearLowerLimit.getChildren().clear();
			multipleSimulationSettingsYearUpperLimit.getChildren().clear();
			simulationsAtEachErlang.getChildren().remove(simulationRepetitions);
			simulationsAtEachErlang.getChildren().remove(numRepetitionsPerErlang);
			multipleSimulationSettingsErlangUpperLimit.getChildren().clear();
			multipleSimulationSettingsErlangLowerLimit.getChildren().clear();
			stepsBetweenErlangs.getChildren().remove(stepBetweenErlangsLabel);
			stepsBetweenErlangs.getChildren().remove(stepBetweenErlangsField);

            singleSimulationSettingsErlang.getChildren().add(erlangLabel);
            singleSimulationSettingsErlang.getChildren().add(erlangIntField);
            singleSimulationSettingsSeed.getChildren().add(seedLabel);
            singleSimulationSettingsSeed.getChildren().add(seedField);
		}

		erlangLabel.setVisible(!erlangLabel.isVisible());
		erlangIntField.setVisible(!erlangIntField.isVisible());
		trafficDataIndexChanged(e);
	}

	@FXML
	public void startSimulation(ActionEvent e) {
		Simulation simulation;

		try {
			Network network = ApplicationResources.getProject().getNetwork();
			boolean selected = false;
			for (Node checkbox : algoCheckBoxContainer.getChildren())
				if (((CheckBox) checkbox).isSelected())
					selected = true;
			// Initial checks
			if ((!runMultipleSimulations.isSelected() && algorithms.getValue() == null) || (runMultipleSimulations.isSelected() && !selected)) {
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
			else if (bestPaths.getValue() > network.getMaxPathsCount() || bestPaths.getValue() <= 0) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle(LocaleUtils.translate("set_number_of_candidate_paths"));
				alert.setHeaderText(null);
				if (bestPaths.getValue() > network.getMaxPathsCount())
					alert.setContentText(LocaleUtils.translate("number_of_candidate_paths_must_be_less_than_best_paths_count"));
				else
					alert.setContentText(LocaleUtils.translate("number_of_requests_must_be_greater_than_zero"));

				bestPaths.setStyle("-fx-border-color: red; -fx-border-width: 1; -fx-border-radius: 2");
				bestPaths.setOnKeyTyped(event -> {
					bestPaths.setStyle("-fx-border-width: 0;");
				});
				alert.setResizable(true);
				alert.getDialogPane().setPrefSize(480.0, 100);
				alert.showAndWait();
				return;
			} else if (demands.getValue() <= 0) {
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
			for (IModulation modulation : network.getAllowedModulations())
				network.disallowModulation(modulation);

			for (IModulation modulation : Settings.registeredModulations.values())
				if (modulations[modulation.getId()].isSelected())
					network.allowModulation(modulation);

			network.setBestPathsCount(bestPaths.getValue());

			//Modulation Metric is always dynamic
			network.setModualtionMetricType(MetricType.DYNAMIC);
			//Regenerator Metric value is always set to 5
			network.setRegeneratorMetricValue(5);
			//Regenerator Metric is always static
			network.setRegeneratorMetricType(MetricType.STATIC);

			cancelButton.setDisable(false);
			setMultipleSimulationsRan(false);
			progressBar.clearData();

			//If multiple simulations is selected then we will create a single thread executor otherwise to run multiple consecutive simulations back-to-back, otherwise, run one task
			if (!runMultipleSimulations.isSelected()) {
				int erlang;
				TrafficGenerator generator = generators.getValue();
				if (!trafficDataYearSelection.getValue().equals(LocaleUtils.translate("custom"))) {
					int trafficYear = Integer.parseInt(trafficDataYearSelection.getValue());
					Project project = ApplicationResources.getProject();
					ResizableCanvas.getParentController().setupGenerators(project, trafficYear);

					erlang = (int) (Settings.DEFAULT_ERLANG * Math.pow((1+ Settings.CAGR), (trafficYear - Settings.DEFAULT_YEAR)));
				}
				else
					erlang = erlangIntField.getValue();

				simulation = new Simulation(network, generator);

				//TODO: REFACTOR SIMULATION TASK INTO SIMULATION
				SimulationTask task = new SimulationTask(simulation, seedField.getValue(), 1, Double.parseDouble(alpha.getText()), erlang, demands.getValue(), true, this);
				progressBar.runTask(task, true, this);
			} else {
				int erlangRangeLow;
				int erlangRangeHigh;
				if (trafficDataYearSelection.getValue() == "Custom") {
					erlangRangeLow = erlangRangeLowField.getValue();
					erlangRangeHigh = erlangRangeHighField.getValue();

					if (erlangRangeLow > erlangRangeHigh || erlangRangeLow == erlangRangeHigh) {
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setTitle(LocaleUtils.translate("erlang_range"));
						alert.setHeaderText(null);
						alert.setContentText(LocaleUtils.translate("lower_erlang_range_must_be_less_than_upper_erlang_range"));
						alert.setResizable(true);
						alert.getDialogPane().setPrefSize(480.0, 100);
						alert.showAndWait();
						return;
					}
				}
				final ExecutorService runMultipleSimulationService = Executors.newSingleThreadExecutor(new ThreadFactory() {
					@Override
					public Thread newThread(Runnable runnable) {
						Thread thread = Executors.defaultThreadFactory().newThread(runnable);
						thread.setDaemon(true);
						return thread;
					}
				});
				ArrayList<IRMSAAlgorithm> algorithms = new ArrayList<>();
				for (Node algorithmBox : algoCheckBoxContainer.getChildren()) {
					if (!((CheckBox) algorithmBox).isSelected())
						continue;
					String algoName = ((CheckBox) algorithmBox).getText();
					for (IRMSAAlgorithm algorithm : Settings.registeredAlgorithms.values())
						if (algorithm.getName().equals(algoName)) {
							algorithms.add(algorithm);
							break;
						}
				}
				ArrayList<ArrayList> tasks = new ArrayList<>();
				if (!trafficDataYearSelection.getValue().equals(LocaleUtils.translate("custom"))){
					TrafficGenerator generator = generators.getValue();
					for (int numRepetitions = 1; numRepetitions <= numRepetitionsPerErlang.getValue(); numRepetitions++) {
						Random random = new Random();
						int randomSeed = random.nextInt(101);
						int trafficYearStart = Integer.parseInt(yearRangeLowField.getValue());
						int trafficYearEnd = Integer.parseInt(yearRangeHighField.getValue());
						TaskReadyProgressBar.addResultsDataSeed(randomSeed);
						for (int trafficYear = trafficYearStart; trafficYear <= trafficYearEnd; trafficYear++) {
							Project project = ApplicationResources.getProject();
							ResizableCanvas.getParentController().setupGenerators(project, trafficYear);
							simulation = new Simulation(network, generator);
							simulation.setMultipleSimulations(true);
							ArrayList taskSettingsArray = new ArrayList();
							taskSettingsArray.add(simulation);
							taskSettingsArray.add(randomSeed);
							taskSettingsArray.add(Double.parseDouble(alpha.getText()));
							taskSettingsArray.add((int) (Settings.DEFAULT_ERLANG * Math.pow((1+ Settings.CAGR), (trafficYear - Settings.DEFAULT_YEAR))));
							taskSettingsArray.add(demands.getValue());
							taskSettingsArray.add(true);
							tasks.add(taskSettingsArray);
						}
					}
				}
				else {
					for (int numRepetitions = 1; numRepetitions <= numRepetitionsPerErlang.getValue(); numRepetitions++) {
						Random random = new Random();
						erlangRangeLow = erlangRangeLowField.getValue();
						erlangRangeHigh = erlangRangeHighField.getValue();
						int randomSeed = random.nextInt(101);
						TaskReadyProgressBar.addResultsDataSeed(randomSeed);
						for (int erlangValue = erlangRangeLow; erlangValue <= erlangRangeHigh; erlangValue += stepBetweenErlangsField.getValue()) {
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
				}
				progressBar.runTasks(algorithms, tasks, true, runMultipleSimulationService, this, network);
			}
		} catch (NullPointerException | MapLoadingException ex) {
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

	public void setMultipleSimulationsRan(boolean multipleSimulationsRan) {
		this.multipleSimulationsRan = multipleSimulationsRan;
	}

	public boolean isMultipleSimulationsRan() {
		return multipleSimulationsRan;
	}

	public void setRunning(boolean isRunning) {
		for (Node node : new Node[]{generators,
				runMultipleSimulations, simulationRepetitions, erlangLabel, stepBetweenErlangsLabel, erlangRangeLabel,
				erlangRangeLowLabel, erlangRangeHighLabel, seedLabel, erlangIntField, erlangRangeLowField, numRepetitionsPerErlang,
				stepBetweenErlangsField, erlangRangeHighField, seedField, alpha, demands, settings,
				multipleSimulatonSettingsLabel, multipleSimulatonSettingsRange, algorithms, allowModulationChange, bestPaths,
				regeneratorsMetricValue
		}) {
			try {
				node.setDisable(isRunning);
			} catch (NullPointerException ignored) {
			}
		}
		pauseButton.setDisable(!isRunning);
		pauseInfoLabel.setVisible(isRunning);
		cancelButton.setDisable(!isRunning);
		StartButton.setDisable(isRunning);
	}

	// Cancel simulation button
	@FXML
	public void cancelSimulation(ActionEvent e) {
		paused = true;

		if (!finished) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle(LocaleUtils.translate("confirmation"));
			alert.setHeaderText(LocaleUtils.translate("cancel_current_simulation"));
			alert.setContentText(LocaleUtils.translate("cancel_current_simulation_confirmation_question"));

			Optional<ButtonType> result = alert.showAndWait();
			//User clicks OK
			if (result.get() == ButtonType.OK) {
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
				if (runMultipleSimulations.isSelected())
					try {
						progressBar.getRunMultipleSimulationService().shutdownNow();
						ResizableCanvas.getParentController().initializeSimulationsAndNetworks();
					} catch (MapLoadingException ex) {
						new ErrorDialog(ex.getMessage(), ex);
						ex.printStackTrace();
						return;
					} catch (Exception ex) {
						new ErrorDialog(LocaleUtils.translate("an_exception_occurred_label"), ex);
						ex.printStackTrace();
						return;
					}
			} else {
				paused = false;
				pauseButton.setText(LocaleUtils.translate("pause_icon"));
				pauseButton.setTooltip(new Tooltip(LocaleUtils.translate("pause_simulation")));
				return;
			}
		}

		settings.setDisable(false);
	}

	@FXML
	public void pauseSimulation(ActionEvent e) {
		if (paused && !finished && started) {
			ResizableCanvas.getParentController().graph.changeState(DrawingState.noActionState);
			pauseButton.setText(LocaleUtils.translate("pause_icon"));
			pauseButton.setTooltip(new Tooltip(LocaleUtils.translate("pause_simulation")));
		} else if (!paused && !finished && started) {
			ResizableCanvas.getParentController().setExpandedPane(2);
			ResizableCanvas.getParentController().whilePaused();
			pauseButton.setText(LocaleUtils.translate("resume_icon"));
			pauseButton.setTooltip(new Tooltip(LocaleUtils.translate("resume_simulation")));
		} else
			return;

		paused ^= true;
	}


	@FXML
	public void clear(ActionEvent e) {
		ResizableCanvas.getParentController().graph.resetCanvas();
	}

	@FXML
	public void disableClearSimulationButton() {
		cancelButton.setDisable(true);
	}

    public void sendMail(String message) {
        String to = emailInput.getText();
		String as = PasswordEncrypter.decrypt(Settings.MAIL_PASSWORD[(int) (Math.random()*Settings.MAIL_PASSWORD.length)]);
        if (emailCheckbox.isSelected() && !(emailCheckbox.getText().equals("Email") || emailCheckbox.getText().equals(""))) {
			try {
				ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP1);
				ExchangeCredentials c = new WebCredentials(Settings.MAIL_USERNAME, as);
				service.setCredentials(c);
				service.setUrl(new URI("https://mail.bcit.ca/ews/exchange.asmx"));
				EmailMessage msg = new EmailMessage(service);
				msg.setSubject(LocaleUtils.translate("email_subject"));
				msg.setBody(MessageBody.getMessageBodyFromText(message));
				msg.getBody().setBodyType(BodyType.Text);
				msg.getToRecipients().add(to);
				if (multipleSimulationsRan) {
					progressBar.initializePDFGen();
					progressBar.generatePDF();
					java.nio.file.Path dir = Paths.get(TaskReadyProgressBar.RESULTS_SUMMARY_DIR_NAME);
					Optional<java.nio.file.Path> lastFilePath = Files.list(dir)
							.filter(f -> !Files.isDirectory(f))
							.max(Comparator.comparingLong(f -> f.toFile().lastModified()));
					lastFilePath.ifPresent(path ->
							{
								try {
									msg.getAttachments().addFileAttachment(path.toAbsolutePath().toString());
								}
								catch (Exception e) {
									e.printStackTrace();
								}
							}
					);
					msg.send();
					lastFilePath.ifPresent(path -> path.toFile().delete());
				}
				else
					msg.send();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
}
