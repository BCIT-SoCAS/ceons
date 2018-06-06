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
import javafx.scene.layout.VBox;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SimulationMenuController {
	
	public static ComboBox<TrafficGenerator> generatorsStatic; // TODO ;_;
	
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

	public static TaskReadyProgressBar progressBar; // TODO ;_;
	
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
	} // TODO Ugly way of doing that...
	
	public RMSAAlgorithm getAlgorithm() {
		return algorithms.getValue();
	}

	public boolean getAllowModulationChange() {
		return allowModulationChange.isSelected();
	}

	public List<Modulation> getModulations() {
		List<Modulation> modulations = new ArrayList<>();
		for (Modulation modulation : Modulation.values()) if (this.modulations[modulation.ordinal()].isSelected())
			modulations.add(modulation);
		return modulations;
	}

	public MetricType getRegeneratorsMetric() {
		return MetricType.valueOf2(((RadioButton) regeneratorsMetric.getSelectedToggle()).getText());
	}
	
	public MetricType getModulationMetric() {
		return MetricType.valueOf2(((RadioButton) modulationMetric.getSelectedToggle()).getText());
	}
	
	public int getCandidatePathsCount() {
		return bestPaths.getValue();
	}
	
	public int getRegeneratorsMetricValue() {
		return regeneratorsMetricValue.getValue();
	}

	@FXML public void startSimulationAction(ActionEvent e) {
		Network network = ApplicationResources.getProject().getNetwork();
		
		if (algorithms.getValue() == null) {
			Logger.info("No algorithm selected!");
			return;
		}
		network.setDemandAllocationAlgorithm(algorithms.getValue());
		
		network.setCanSwitchModulation(allowModulationChange.isSelected());
		for (Toggle toggle : modulationMetric.getToggles()) if (toggle.isSelected())
			network.setModualtionMetricType(MetricType.valueOf2(((RadioButton) toggle).getText()));
		for (Modulation modulation : network.getAllowedModulations()) network.disallowModulation(modulation);
		for (Modulation modulation : Modulation.values()) if (modulations[modulation.ordinal()].isSelected()) 
				network.allowModulation(modulation);
		
		network.setRegeneratorMetricValue(regeneratorsMetricValue.getValue());
		for (Toggle toggle : regeneratorsMetric.getToggles()) if (toggle.isSelected())
			network.setRegeneratorMetricType(MetricType.valueOf2(((RadioButton) toggle).getText()));
		
		network.setBestPathsCount(bestPaths.getValue());
		
//		settings.disableProperty().set(true);
		Simulation simulation = new Simulation(network, generators.getValue());
		SimulationTask task = new SimulationTask(simulation, seed.getValue(), Double.parseDouble(alpha.getText()), erlang.getValue(), demands.getValue(), replicaPreservation.isSelected());
		progressBar.runTask(task, true);
	}
}
