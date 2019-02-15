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
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import ca.bcit.net.algo.AMRA;
import ca.bcit.io.project.EONProject;
import ca.bcit.io.project.EONProjectFileFormat;
import ca.bcit.io.project.Project;
import ca.bcit.io.project.ProjectFileFormat;
import java.io.File;
import ca.bcit.io.YamlSerializable;

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
	private CheckBox[] modulations;

	public static TaskReadyProgressBar progressBar;
	
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
	
	@FXML public void startSimulationAction(ActionEvent e) {
		Network network = ApplicationResources.getProject().getNetwork();

		if (algorithms.getValue() == null) {
			Logger.info("No algorithm selected!");
			return;
		}

		network.setDemandAllocationAlgorithm(algorithms.getValue());

		network.setCanSwitchModulation(allowModulationChange.isSelected());
		for (Toggle toggle : modulationMetric.getToggles())
			if (toggle.isSelected())
				network.setModualtionMetricType(MetricType.valueOf2(((RadioButton) toggle).getText()));
		for (Modulation modulation : network.getAllowedModulations()) network.disallowModulation(modulation);
		for (Modulation modulation : Modulation.values())
			if (modulations[modulation.ordinal()].isSelected())
				network.allowModulation(modulation);

		network.setRegeneratorMetricValue(regeneratorsMetricValue.getValue());
		for (Toggle toggle : regeneratorsMetric.getToggles())
			if (toggle.isSelected())
				network.setRegeneratorMetricType(MetricType.valueOf2(((RadioButton) toggle).getText()));

		network.setBestPathsCount(bestPaths.getValue());

//		settings.disableProperty().set(true);
		Simulation simulation = new Simulation(network, generators.getValue());
		SimulationTask task = new SimulationTask(simulation, seed.getValue(), Double.parseDouble(alpha.getText()), erlang.getValue(), demands.getValue(), replicaPreservation.isSelected());
		settings.setDisable(true);
		progressBar.runTask(task, true);
	}

}
