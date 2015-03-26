package mtk.eon.jfx.controllers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
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
import mtk.eon.net.algo.RMSAAlgorithm;

import com.sun.javafx.collections.ObservableListWrapper;

public class SimulationMenuController {
	
	@FXML private VBox settings;
	@FXML private ComboBox<RMSAAlgorithm> algorithms;
	@FXML private ToggleGroup regeneratorsMetric;
	@FXML private ToggleGroup modulationMetric;
	@FXML private CheckBox allowModulationChange;
	@FXML private UIntField bestPaths;
	@FXML private UIntField regeneratorsMetricValue;
	private CheckBox[] modulations;

	private TaskReadyProgressBar progressBar;
	
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
	}
	
	void setProgressBar(TaskReadyProgressBar progressBar) {
		this.progressBar = progressBar;
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
		
		settings.disableProperty().set(true);
		SimulationTask task = new SimulationTask();
		progressBar.runTask(task, true);
	}
}
