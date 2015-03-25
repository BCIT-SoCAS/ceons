package mtk.eon.jfx.controllers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import mtk.eon.jfx.DrawingState;
import mtk.eon.jfx.LinkPropertiesController;
import mtk.eon.jfx.NodePropertiesController;
import mtk.eon.jfx.components.Console;
import mtk.eon.jfx.components.ResizableCanvas;
import mtk.eon.jfx.components.TaskReadyProgressBar;
import mtk.eon.jfx.components.UIntField;
import mtk.eon.jfx.tasks.ProjectLoadingTask;
import mtk.eon.jfx.tasks.SimulationTask;
import mtk.eon.net.MetricType;
import mtk.eon.net.Modulation;
import mtk.eon.net.Network;
import mtk.eon.net.NetworkNode;
import mtk.eon.net.algo.RMSAAlgorithm;
import mtk.eon.utils.Utils;

import com.sun.javafx.collections.ObservableListWrapper;

public class MainWindowController  {
	
	@FXML private Console console;	
	@FXML private TaskReadyProgressBar progressBar;
	@FXML private Label progressLabel;
	
	@FXML private SimulationMenuController simulationMenuController;
	@FXML private NetworkMenuController networkMenuController;
	
	@FXML private ResizableCanvas graph; 
	@FXML private RadioButton RBNoneChose;
	@FXML private Accordion accordion;
	@FXML private TitledPane propertiesTitledPane;
	private final static int PROPERTIES_PANE_NUMBER=4;
	private final static int EDIT_PANE_NUMBER=3;
	
	@FXML private void nodeChose(ActionEvent e) 
	{
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
		for (Field field : MainWindowController.class.getDeclaredFields()) if (field.isAnnotationPresent(FXML.class))
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
		
		graph.init(this);
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

    private void setSelectedPaneContent(TitledPane tp)
    {
        if(tp!=null)
            propertiesTitledPane.setContent(tp.getContent());
        else
            propertiesTitledPane.setContent(null);
    }

}
