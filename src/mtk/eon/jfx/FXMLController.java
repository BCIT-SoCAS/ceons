package mtk.eon.jfx;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import mtk.eon.ApplicationResources;
import mtk.eon.io.Logger;
import mtk.eon.jfx.components.Console;
import mtk.eon.jfx.components.TaskReadyProgressBar;
import mtk.eon.jfx.components.UIntField;
import mtk.eon.jfx.tasks.ProjectLoadingTask;
import mtk.eon.jfx.tasks.SimulationTask;
import mtk.eon.net.MetricType;
import mtk.eon.net.Modulation;
import mtk.eon.net.Network;
import mtk.eon.net.NetworkNode;
import mtk.eon.net.algo.RMSAAlgorithm;
import mtk.general.Utils;

import com.sun.javafx.collections.ObservableListWrapper;

public class FXMLController {
	
	@FXML private Console console;
	
	@FXML private TaskReadyProgressBar progressBar;
	@FXML private Label progressLabel;
	
	@FXML private VBox settings;
	@FXML private ComboBox<RMSAAlgorithm> algorithms;
	@FXML private ToggleGroup regeneratorsMetric;
	@FXML private ToggleGroup modulationMetric;
	@FXML private CheckBox allowModulationChange;
	@FXML private UIntField bestPaths;
	@FXML private UIntField regeneratorsMetricValue;
	private CheckBox[] modulations;
	
	@FXML public void initialize() {
		for (Field field : FXMLController.class.getDeclaredFields()) if (field.isAnnotationPresent(FXML.class))
			try {
				assert field.get(this) != null : "Id '" + field.getName() + "' was not injected!";
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		
		try {
			Utils.setStaticFinal(Console.class, "cout", console.out);
			Utils.setStaticFinal(Console.class, "cin", console.in);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		
		algorithms.setItems(new ObservableListWrapper<RMSAAlgorithm>(new ArrayList<RMSAAlgorithm>(RMSAAlgorithm.getRegisteredAlgorithms())));
		
		modulations = new CheckBox[Modulation.values().length];
		for (Modulation modulation : Modulation.values())
			modulations[modulation.ordinal()] = ((CheckBox) settings.lookup("#modulation" + modulation.ordinal()));
	}
	
	@FXML public void loadNetworkAction(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File file = fileChooser.showOpenDialog(settings.getScene().getWindow());
		
		ProjectLoadingTask task = new ProjectLoadingTask(file);
		Thread thread = new Thread(task);
		thread.start();
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

	int i;
	@FXML public void testButton(ActionEvent e) {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				Network network = ApplicationResources.getProject().getNetwork();
				
				for (NetworkNode node : network.getNodes()) {
					Console.cout.println(node.getName() + " - " + node.getID() + " - " + (node.isReplica() ? "Is replica" : "Isn't replica") + " - Free regs: " + node.getFreeRegenerators());
				}
				i = 1;
				int p = 0;
				try {
					p = network.calculatePaths(() -> updateProgress(i++, network.relationsSize()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				Console.cout.println("Max best paths count: " + p);
				
				return null;
			}
			
			
		};
		progressBar.runTask(task, true);
	}
}
