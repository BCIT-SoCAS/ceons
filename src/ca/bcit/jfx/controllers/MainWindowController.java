package ca.bcit.jfx.controllers;

import ca.bcit.ApplicationResources;
import ca.bcit.drawing.Figure;
import ca.bcit.drawing.FigureControl;
import ca.bcit.drawing.Link;
import ca.bcit.drawing.Node;
import ca.bcit.io.Logger;
import ca.bcit.io.project.Project;
import ca.bcit.io.project.ProjectFileFormat;
import ca.bcit.jfx.DrawingState;
import ca.bcit.jfx.LinkPropertiesController;
import ca.bcit.jfx.NodePropertiesController;
import ca.bcit.jfx.components.Console;
import ca.bcit.jfx.components.CreateNetworkDialog;
import ca.bcit.jfx.components.ResizableCanvas;
import ca.bcit.jfx.components.TaskReadyProgressBar;
import ca.bcit.net.Network;
import ca.bcit.net.NetworkNode;
import ca.bcit.net.demand.generator.AnycastDemandGenerator;
import ca.bcit.net.demand.generator.DemandGenerator;
import ca.bcit.net.demand.generator.TrafficGenerator;
import ca.bcit.net.demand.generator.UnicastDemandGenerator;
import ca.bcit.utils.Utils;
import ca.bcit.utils.random.ConstantRandomVariable;
import ca.bcit.utils.random.MappedRandomVariable;
import ca.bcit.utils.random.UniformRandomVariable;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainWindowController  {
	
	@FXML private Console console;
	@FXML private CreateNetworkDialog CreateNetworkDialog;
	@FXML private TaskReadyProgressBar progressBar;
	@FXML private Label progressLabel;
	@FXML private SimulationMenuController simulationMenuController;
	@FXML private ResizableCanvas graph;
	@FXML private RadioButton RBNoneChose;
	@FXML private Accordion accordion;
	@FXML private TitledPane propertiesTitledPane;
	@FXML private Button PauseButton;
	@FXML private ImageView mapViewer;
	@FXML private Alert alert;
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
		simulationMenuController.setProgressBar(progressBar);

	}
	public void loadProperties(Figure fig, FigureControl list)
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/NodeProperties.fxml"));
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/LinkProperties.fxml"));
        try {
            properties = fxmlLoader.load();
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
    private void setExpadedPane(int idx)
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


    private int i;
	/**
	 * Display dialog window
	 */
	@FXML public void showSelectNetworkOptionDialog(ActionEvent e) {
		CreateNetworkDialog.display();
	}

	@FXML public void onNew(ActionEvent a) {
		Stage dialogWindow = new Stage();
		dialogWindow.initModality(Modality.APPLICATION_MODAL);
		dialogWindow.setTitle("Choose Topology Option");

		Button loadNetworkBtn = new Button("Load Network Topology");
		Button createNewBtn = new Button("Create Network Topology");

		loadNetworkBtn.setPrefWidth(220);
		createNewBtn.setPrefWidth(220);

		loadNetworkBtn.setOnAction(e -> dialogWindow.close());
		createNewBtn.setOnAction(e -> dialogWindow.close());

		HBox layout = new HBox(10);
		layout.getChildren().addAll(loadNetworkBtn, createNewBtn);
		layout.setAlignment(Pos.CENTER);
		layout.setSpacing(20);
		Scene scene = new Scene(layout, 520, 300);
		dialogWindow.setScene(scene);
		dialogWindow.showAndWait();
	}

	@FXML public void onSave(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(ProjectFileFormat.getExtensionFilters());
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		final File file = fileChooser.showSaveDialog(null);

		if (file == null) return;
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() {
				try {
					Logger.info("Saving project to " + file.getName() + "...");
					ProjectFileFormat.getFileFormat(fileChooser.getSelectedExtensionFilter()).save(file, ApplicationResources.getProject());
					Logger.info("Finished saving project.");
				} catch (Exception ex) {
					Logger.info("An exception occurred while saving the project.");
					Logger.debug(ex);
				}
				return null;
			}
		};
		task.run();
	}

	@FXML public void onLoad(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(ProjectFileFormat.getExtensionFilters());
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		final File file = fileChooser.showOpenDialog(null);

		if (file == null) return;
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() {
				try {
					Logger.info("Loading project from " + file.getName() + "...");
					Project project = ProjectFileFormat.getFileFormat(fileChooser.getSelectedExtensionFilter()).load(file);
					ApplicationResources.setProject(project);
					Logger.info("Finished loading project.");
					graph.resetCanvas();
					mapViewer.setImage(new Image(project.getMap()));
					for (NetworkNode n: project.getNetwork().getNodes()){
						n.setRegeneratorsCount(100);
						System.out.println(n.toString());
						graph.addNode(n.getPosition(), n.getName());
						for (NetworkNode n2: project.getNetwork().getNodes()){
							if(project.getNetwork().containsLink(n, n2)) {
								graph.addLink(n.getPosition(), n2.getPosition());
							}
						}
					}

					setupGenerators(project);

				} catch (Exception ex) {
					Logger.info("An exception occurred while loading the project.");
					Logger.debug(ex);
				}
				return null;
			}
		};
		task.run();


		Task<Void> task2 = new Task<Void>() {

			@Override
			protected Void call() {
				Network network = ApplicationResources.getProject().getNetwork();

				i = 1;
				try {
					network.maxPathsCount = network.calculatePaths(() -> updateProgress(i++, network.getNodesPairsCount()));
				} catch (Throwable e) {
					e.printStackTrace();
				}
				Console.cout.println("Max best paths count: " + network.maxPathsCount);

				return null;
			}


		};
		SimulationMenuController.progressBar.runTask(task2, true);
	}

	private void setupGenerators(Project project) {
		Network network = project.getNetwork();
		List<TrafficGenerator> generators = project.getTrafficGenerators();

		List<MappedRandomVariable.Entry<DemandGenerator<?>>> subGenerators = new ArrayList<>();

		subGenerators.add(new MappedRandomVariable.Entry<>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new ConstantRandomVariable<>(false),
				new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 210, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new UniformRandomVariable.Generic<>(network.getNodes()),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getGroup("replicas")), new UniformRandomVariable.Generic<>(network.getGroup("replicas")),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(40, 410, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new UniformRandomVariable.Generic<>(network.getGroup("international")),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getGroup("international")), new UniformRandomVariable.Generic<>(network.getNodes()),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));

		generators.add(new TrafficGenerator("No Backup", new MappedRandomVariable<>(subGenerators)));

		subGenerators = new ArrayList<>();

		subGenerators.add(new MappedRandomVariable.Entry<>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new ConstantRandomVariable<>(false),
				new ConstantRandomVariable<>(true), new UniformRandomVariable.Integer(10, 210, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new UniformRandomVariable.Generic<>(network.getNodes()),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(true), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getGroup("replicas")), new UniformRandomVariable.Generic<>(network.getGroup("replicas")),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(true), new UniformRandomVariable.Integer(40, 410, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new UniformRandomVariable.Generic<>(network.getGroup("international")),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(true), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getGroup("international")), new UniformRandomVariable.Generic<>(network.getNodes()),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(true), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));

		generators.add(new TrafficGenerator("Dedicated Backup", new MappedRandomVariable<>(subGenerators)));

		subGenerators = new ArrayList<>();

		subGenerators.add(new MappedRandomVariable.Entry<>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new ConstantRandomVariable<>(false),
				new ConstantRandomVariable<>(true), new UniformRandomVariable.Integer(10, 210, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new UniformRandomVariable.Generic<>(network.getNodes()),
				new ConstantRandomVariable<>(true), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getGroup("replicas")), new UniformRandomVariable.Generic<>(network.getGroup("replicas")),
				new ConstantRandomVariable<>(true), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(40, 410, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new UniformRandomVariable.Generic<>(network.getGroup("international")),
				new ConstantRandomVariable<>(true), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getGroup("international")), new UniformRandomVariable.Generic<>(network.getNodes()),
				new ConstantRandomVariable<>(true), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));

		subGenerators.add(new MappedRandomVariable.Entry<>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new ConstantRandomVariable<>(false),
				new ConstantRandomVariable<>(true), new UniformRandomVariable.Integer(10, 210, 10), new ConstantRandomVariable<>(0.5f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new UniformRandomVariable.Generic<>(network.getNodes()),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(true), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(0.5f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getGroup("replicas")), new UniformRandomVariable.Generic<>(network.getGroup("replicas")),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(true), new UniformRandomVariable.Integer(40, 410, 10), new ConstantRandomVariable<>(0.5f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new UniformRandomVariable.Generic<>(network.getGroup("international")),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(true), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(0.5f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getGroup("international")), new UniformRandomVariable.Generic<>(network.getNodes()),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(true), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(0.5f))));

		subGenerators.add(new MappedRandomVariable.Entry<>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new ConstantRandomVariable<>(true),
				new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 210, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new UniformRandomVariable.Generic<>(network.getNodes()),
				new ConstantRandomVariable<>(true), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getGroup("replicas")), new UniformRandomVariable.Generic<>(network.getGroup("replicas")),
				new ConstantRandomVariable<>(true), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(40, 410, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new UniformRandomVariable.Generic<>(network.getGroup("international")),
				new ConstantRandomVariable<>(true), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getGroup("international")), new UniformRandomVariable.Generic<>(network.getNodes()),
				new ConstantRandomVariable<>(true), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));

		generators.add(new TrafficGenerator("Shared Backup", new MappedRandomVariable<>(subGenerators)));

		SimulationMenuController.generatorsStatic.setItems(new ObservableListWrapper<>(generators));
	}

}
