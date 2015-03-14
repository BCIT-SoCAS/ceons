package mtk.eon.jfx;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import mtk.eon.ApplicationResources;
import mtk.eon.drawing.Figure;
import mtk.eon.drawing.FigureControl;
import mtk.eon.drawing.Link;
import mtk.eon.drawing.Node;
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
import mtk.eon.net.algo.Algorithm;
import mtk.general.Utils;
import mtk.geom.Vector2F;

import com.sun.javafx.collections.ObservableListWrapper;

public class FXMLController  {
	
	@FXML private Console console;
	
	@FXML private TaskReadyProgressBar progressBar;
	@FXML private Label progressLabel;
	@FXML private ResizableCanvas graph; 
	@FXML private VBox settings;
	@FXML private ComboBox<Algorithm> algorithms;
	@FXML private ToggleGroup regeneratorsMetric;
	@FXML private ToggleGroup modulationMetric;
	@FXML private CheckBox allowModulationChange;
	@FXML private UIntField bestPaths;
	@FXML private UIntField regeneratorsMetricValue;
	@FXML private RadioButton RBNoneChose;
	@FXML private Accordion accordion;
	@FXML private TitledPane propertiesTitledPane;
	private final static int PROPERTIES_PANE_NUMBER=4;
	private final static int EDIT_PANE_NUMBER=3;
	private CheckBox[] modulations;
	
	@FXML private void nodeChose(ActionEvent e) 
	{
		System.out.println(progressLabel);
        graph.changeState(DrawingState.nodeAddingState);

	}
	@FXML private void linkChose(ActionEvent e) 
	{
		graph.changeState(DrawingState.linkAddingState);
	}
	@FXML private void noneChose(ActionEvent e) 
	{
		graph.changeState(DrawingState.clickingState);
	}
	@FXML private void deleteNodeChose(ActionEvent e)
	{
		graph.changeState(DrawingState.nodeDeleteState);
	}
	@FXML private void deleteLinkChose(ActionEvent e)
	{
		graph.changeState(DrawingState.linkDeleteState);
	}
	@FXML private void deleteFewElementsChose(ActionEvent e)
	{
		graph.changeState(DrawingState.fewElementsDeleteState);
	}
	@FXML private void rotateAroundCenterChose(ActionEvent e)
	{
		graph.changeState(DrawingState.rotateAroundCenter);
	}	
	@FXML private void rotateAroundNodeChose(ActionEvent e)
	{
		graph.changeState(DrawingState.rotateAroundNode);
	}
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
		algorithms.setItems(new ObservableListWrapper<Algorithm>(new ArrayList<Algorithm>(Algorithm.getRegisteredAlgorithms())));
		
		modulations = new CheckBox[Modulation.values().length];
		for (Modulation modulation : Modulation.values())
			modulations[modulation.ordinal()] = ((CheckBox) settings.lookup("#modulation" + modulation.ordinal()));
		graph.init(this);
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
	public void loadProperties(Figure fig,FigureControl list)
	{
		 {
			    if(fig instanceof Node)
			    {
			        loadNodeProperties(fig,list);
			    }
			    else if (fig instanceof Link)
			    {
			        loadLinkProperties(fig,list);
			    }
			    else
			        loadEmptyProperties();
			    }
	}
	private void loadEmptyProperties()
	{
		setExpadedPane(EDIT_PANE_NUMBER);
	}
	private void loadNodeProperties(Figure temp,FigureControl list) {
        TitledPane properties = new TitledPane();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mtk/eon/jfx/res/NodeProperties.fxml"));
        try {
            properties = (TitledPane) fxmlLoader.load();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        NodePropertiesController controller = fxmlLoader.<NodePropertiesController>getController();
        if (controller != null) {
            controller.initDate(list, temp);
        }
        setSelectedPaneContent(properties);
        setExpadedPane(PROPERTIES_PANE_NUMBER);
    }
	private void loadLinkProperties(Figure temp,FigureControl list) {
        TitledPane properties = new TitledPane();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mtk/eon/jfx/res/LinkProperties.fxml"));
        try {
            properties = (TitledPane) fxmlLoader.load();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        LinkPropertiesController controller = fxmlLoader.<LinkPropertiesController>getController();
        if (controller != null) {
            controller.initDate(list, temp);
        }
        setSelectedPaneContent(properties);
        setExpadedPane(PROPERTIES_PANE_NUMBER);
    }
    public void setExpadedPane(int idx)
    {
        accordion.getPanes().get(idx).setExpanded(true);
    }
	public void setNoneRadioButtonActive()
	{
		RBNoneChose.setSelected(true);
		RBNoneChose.requestFocus();
	}
    private void setSelectedPaneContent(TitledPane tp)
    {
        if(tp!=null)
            propertiesTitledPane.setContent(tp.getContent());
        else
            propertiesTitledPane.setContent(null);
    }
    @FXML private void canvasOnMouseClicked(MouseEvent e)
    {
    	graph.canvasOnMouseClicked(e);
    }
    @FXML private void canvasOnMousePressed(MouseEvent e)
    {
    	graph.canvasOnMousePressed(e);
    }
    @FXML private void canvasOnMouseReleased(MouseEvent e)
    {
    	graph.canvasOnMouseReleased(e);
    }
    @FXML private void canvasOnMouseDragged(MouseEvent e)
    {
    	graph.canvasOnMouseDragged(e);
    }
    @FXML private void canvasOnMouseScroll(ScrollEvent e)
    {
    	graph.canvasOnMouseScroll(e);
    }

}
