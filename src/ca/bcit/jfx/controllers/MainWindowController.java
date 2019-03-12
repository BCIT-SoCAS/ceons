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
import ca.bcit.jfx.components.Console;
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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import com.google.maps.GeoApiContext;
import com.google.maps.StaticMapsApi;
import com.google.maps.model.Size;
import com.google.maps.ImageResult;
import javafx.util.Duration;

public class MainWindowController {


    @FXML
    private Console console;
    @FXML
    private TaskReadyProgressBar progressBar;
    @FXML
    private Label progressLabel;
    @FXML
    private SimulationMenuController simulationMenuController;
    @FXML
    public ResizableCanvas graph;
    @FXML
    private Accordion accordion;
    @FXML
    private Label info;
    @FXML
    private TitledPane liveInfoPane;
    @FXML
    private ImageView mapViewer;

    private final static int PROPERTIES_PANE_NUMBER = 2;

    @FXML
    private void nodeAdd(ActionEvent e) {
        graph.changeState(DrawingState.nodeAddingState);
    }

    @FXML
    private void linkAdd(ActionEvent e) {
        graph.changeState(DrawingState.linkAddingState);
    }

    @FXML
    private void nodeSelect(ActionEvent e) { graph.changeState(DrawingState.clickingState); }

    @FXML
    private void deleteNodeChose(ActionEvent e) {
        graph.changeState(DrawingState.nodeDeleteState);
    }

    @FXML
    private void deleteLinkChose(ActionEvent e) {
        graph.changeState(DrawingState.linkDeleteState);
	}
	
	@FXML
    private void deleteFewElementsChose(ActionEvent e) {
        graph.changeState(DrawingState.fewElementsDeleteState);
    }

    @FXML
    private void nodeMarkReplica(ActionEvent e) {
        graph.changeState(DrawingState.nodeMarkReplicaState);
	}

	@FXML
    private void nodeMarkInternational(ActionEvent e) {
        graph.changeState(DrawingState.nodeMarkInternationalState);
	}
	
	@FXML
    private void nodeUnmark(ActionEvent e) {
        graph.changeState(DrawingState.nodeUnmarkState);
    }

    @FXML
    public void initialize() {
        for (Field field : MainWindowController.class.getDeclaredFields())
            if (field.isAnnotationPresent(FXML.class))
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

    /**
     * Opens up live info tab with the node/link information
     *
     * @param fig   node or link that is selected
     * @param list  list of all links & nodes
     */
    public void loadProperties(Figure fig, FigureControl list) {
        {
            if (fig instanceof Node) {
                loadNodeProperties(fig, list);
            } else if (fig instanceof Link) {
                loadLinkProperties(fig, list);
            } else
                loadCurrentSummary(this.spectrumBlockedVolume, this.regeneratorsBlockedVolume, this.linkFailureBlockedVolume, this.totalVolume);
        }
    }

    private void loadCurrentSummary(double spectrumBlocked, double regeneratorsBlocked, double linkFailureBlocked, double totalVolume) {
        TitledPane properties = new TitledPane();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/views/LiveInfoSummary.fxml"));
        try {
            properties = (TitledPane) fxmlLoader.load();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        LiveInfoSummaryController controller = fxmlLoader.<LiveInfoSummaryController>getController();
        if (controller != null) {
            controller.fillInformation(spectrumBlocked, regeneratorsBlocked, linkFailureBlocked, totalVolume);
        }
        setLiveInfoPaneContent(properties);
        setExpandedPane(PROPERTIES_PANE_NUMBER);
    }

    private void loadNodeProperties(Figure temp, FigureControl list) {
        TitledPane properties = new TitledPane();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/views/NodeProperties.fxml"));
        try {
            properties = (TitledPane) fxmlLoader.load();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        NodePropertiesController controller = fxmlLoader.<NodePropertiesController>getController();
        if (controller != null) {
            controller.initData(list, temp);
        }
        setLiveInfoPaneContent(properties);
        setExpandedPane(PROPERTIES_PANE_NUMBER);
    }

    private void loadLinkProperties(Figure temp, FigureControl list) {
        TitledPane properties = new TitledPane();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/views/LinkProperties.fxml"));
        try {
            properties = fxmlLoader.load();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        LinkPropertiesController controller = fxmlLoader.<LinkPropertiesController>getController();
        if (controller != null) {
            controller.initData(list, temp);
        }
        setLiveInfoPaneContent(properties);
        setExpandedPane(PROPERTIES_PANE_NUMBER);
    }

    public void setExpandedPane(int idx) {
        accordion.getPanes().get(idx).setExpanded(true);
    }

    private void setLiveInfoPaneContent(TitledPane tp) {
        if (tp != null)
            liveInfoPane.setContent(tp.getContent());
        else
            liveInfoPane.setContent(null);
    }

    private boolean validateAPIkey(String key) {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(key)
                .build();
        Size mapSize = new Size(200, 200);
        try {
            ImageResult map = StaticMapsApi.newRequest(context, mapSize).center("Vancouver").zoom(100).await();
            System.out.println(map.contentType);
            return true;
        } catch (Exception ex) {
            Logger.info("Invalid API key");
            return false;
        }
    }

    private void writeAPIkeyToFile(String apiKey, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(apiKey);
            writer.close();
        } catch (IOException ex) {
            Logger.info("An exception occurred while saving API key");
            Logger.debug(ex);
        }
    }

    private int i;

    private void saveAPIkey(ActionEvent e, TextField inputField, Stage dialogWindow) {
        String apiKey = inputField.getText();
        if (!validateAPIkey(apiKey)) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Warning");
			alert.setHeaderText("API key is Invalid");
            alert.showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("api_key");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        final File file = fileChooser.showSaveDialog(null);

        if (file == null) return;
        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() {
                Logger.info("Saving API key to " + file.getName() + " file");
                writeAPIkeyToFile(apiKey, file);
				Logger.info("Finished saving API key");
				dialogWindow.close();
                return null;
            }
        };
        task.run();
	}
	
	private void displaySaveAPIKeyDialog() {
		Stage dialogWindow = new Stage();
		dialogWindow.initModality(Modality.APPLICATION_MODAL);
		dialogWindow.setTitle("Save Google Maps API key");
		dialogWindow.getIcons().add(new Image("/ca/bcit/jfx/res/images/LogoBCIT.png"));

		Text title = new Text("Save Google Maps API key to a file");
		title.setFill(Color.web("#e5e3e3"));
		title.setFont(new Font(20));

		TextField saveKeyInput = new TextField();
		saveKeyInput.setPromptText("Please enter Google Maps API key");
		Button saveAPIkeyBtn = new Button("Save");
		Button closeBtn = new Button("Cancel");

		saveAPIkeyBtn.setPrefWidth(220);
		closeBtn.setPrefWidth(220);

		saveAPIkeyBtn.setOnAction(e -> saveAPIkey(e, saveKeyInput, dialogWindow));
		closeBtn.setOnAction(e -> dialogWindow.close());

		GridPane grid = new GridPane();
		grid.setStyle("-fx-background-image: url(\"/ca/bcit/jfx/res/images/bg.png\");"
					+ " -fx-background-size: cover;");
		dialogWindow.getIcons().add(new Image("/ca/bcit/jfx/res/images/LogoBCIT.png"));
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(20);
		grid.setPadding(new Insets(25, 25, 25, 25));

		grid.add(title, 0, 0, 2, 1);
		grid.add(saveKeyInput, 0, 1, 2, 1);
		grid.add(saveAPIkeyBtn, 0, 2);
		grid.add(closeBtn, 1, 2);
		Scene scene = new Scene(grid, 520, 300);
		dialogWindow.setScene(scene);
		dialogWindow.showAndWait();
	}

    @FXML
    public void onNew(ActionEvent a) {
		String rootPath = System.getProperty("user.dir");
		Path apiKeyPath = Paths.get(rootPath + "/api_key.txt");
		if (Files.exists(apiKeyPath)){
			// logic for saving new topology
			return;
		} else {
			displaySaveAPIKeyDialog();
		}
    }

    @FXML
    public void onSave(ActionEvent e) {
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

    private static Timeline updateTimeline;

    public void updateGraph() {
        updateTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        event -> {
                            try {
                                graph.resetCanvas();
                                Project project = ApplicationResources.getProject();
                                for (NetworkNode n : project.getNetwork().getNodes()) {
                                    graph.addNetworkNode(n);
                                    for (NetworkNode n2 : project.getNetwork().getNodes()) {
                                        if (project.getNetwork().containsLink(n, n2)) {
                                            int totalSlices = project.getNetwork().getLinkSlices(n, n2).getSlicesCount();
                                            int occupiedSlices = project.getNetwork().getLinkSlices(n, n2).getOccupiedSlices();
                                            int currentPercentage = (totalSlices - occupiedSlices) * 100 / totalSlices;
                                            graph.addLink(n.getPosition(), n2.getPosition(), currentPercentage);
                                        }
                                    }
                                }

                            } catch (Exception ex) {
                                Logger.debug("An exception on updating the network UI");
                            }
                        }
                ),
                new KeyFrame(Duration.seconds(0.5))
        );
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }

    public void stopUpdateGraph() {
        System.out.println("Timeline stopped");
        updateTimeline.stop();
    }

    public void resetGraph() {
        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() {
                try {
                    stopUpdateGraph();
                    graph.resetCanvas();
                    for (NetworkNode n : ApplicationResources.getProject().getNetwork().getNodes()) {
                        n.clearOccupied();
                        n.setRegeneratorsCount(100);
                        n.setFigure(n);
                        graph.addNetworkNode(n);
                        for (NetworkNode n2 : ApplicationResources.getProject().getNetwork().getNodes()) {
                            if (ApplicationResources.getProject().getNetwork().containsLink(n, n2)) {
                                graph.addLink(n.getPosition(), n2.getPosition(), 100);
                            }
                        }
                    }

                } catch (Exception ex) {
                    Logger.info("An exception occurred while updating the project.");
                    Logger.debug(ex);
                }
                return null;
            }
        };
        task.run();
    }

    @FXML
    public void onLoad(ActionEvent e) {
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
                    //for every node in the network place onto map and for each node add links between
                    for (NetworkNode n : project.getNetwork().getNodes()) {
                        n.setRegeneratorsCount(100);
                        n.setFigure(n);
                        graph.addNetworkNode(n);
                        for (NetworkNode n2 : project.getNetwork().getNodes()) {
                            if (project.getNetwork().containsLink(n, n2)) {
                                graph.addLink(n.getPosition(), n2.getPosition(), 100);
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

    public double totalVolume;
    public double spectrumBlockedVolume;
    public double regeneratorsBlockedVolume;
    public double linkFailureBlockedVolume;

    @FXML
    public void whilePaused() {
        graph.changeState(DrawingState.clickingState);
        info.setText("Blocked Spectrum: " + this.spectrumBlockedVolume / this.totalVolume * 100 + "%" + "\n"
                + "Blocked Regenerators: " + this.regeneratorsBlockedVolume / this.totalVolume * 100 + "%" + "\n"
                + "Blocked Link Failure: " + this.linkFailureBlockedVolume / this.totalVolume * 100 + "%");

//        Project project = ApplicationResources.getProject();
//        for (NetworkNode n : project.getNetwork().getNodes()) {
//            info.setText(info.getText() + n.getName() + " has " + n.getFreeRegenerators() + " free regenerators " + '\n');
//            for (NetworkNode n2 : project.getNetwork().getNodes()) {
//                if (project.getNetwork().containsLink(n, n2)) {
//                    int totalSlices = project.getNetwork().getLinkSlices(n, n2).getSlicesCount();
//                    int occupiedSlices = project.getNetwork().getLinkSlices(n, n2).getOccupiedSlices();
//                    int currentPercentage = (totalSlices - occupiedSlices) * 100 / totalSlices;
//                }
//            }
//        }
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
