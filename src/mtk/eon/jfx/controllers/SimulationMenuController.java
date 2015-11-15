package mtk.eon.jfx.controllers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import mtk.eon.ApplicationResources;
import mtk.eon.io.Logger;
import mtk.eon.jfx.components.TaskReadyProgressBar;
import mtk.eon.jfx.components.UIntField;
import mtk.eon.jfx.tasks.SimulationTask;
import mtk.eon.net.MetricType;
import mtk.eon.net.Modulation;
import mtk.eon.net.Network;
import mtk.eon.net.Simulation;
import mtk.eon.net.algo.RMSAAlgorithm;
import mtk.eon.net.demand.generator.TrafficGenerator;

import com.sun.javafx.collections.ObservableListWrapper;

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
		algorithms.setItems(new ObservableListWrapper<RMSAAlgorithm>(new ArrayList<RMSAAlgorithm>(RMSAAlgorithm.getRegisteredAlgorithms())));
		
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
		List<Modulation> modulations = new ArrayList<Modulation>();
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
		Simulation simulation = new Simulation(network, algorithms.getValue(), generators.getValue());
		SimulationTask task = new SimulationTask(simulation, seed.getValue(), Double.parseDouble(alpha.getText()), erlang.getValue(), demands.getValue(), replicaPreservation.isSelected());
		progressBar.runTask(task, true);
	}
}
