package ca.bcit.jfx.controllers;

import ca.bcit.ApplicationResources;
import ca.bcit.drawing.Figure;
import ca.bcit.drawing.FigureControl;
import ca.bcit.drawing.Link;
import ca.bcit.drawing.Node;
import ca.bcit.io.Logger;
import ca.bcit.io.MapLoadingException;
import ca.bcit.io.project.Project;
import ca.bcit.io.project.ProjectFileFormat;
import ca.bcit.jfx.DrawingState;
import ca.bcit.jfx.components.Console;
import ca.bcit.jfx.components.ErrorDialog;
import ca.bcit.jfx.components.ResizableCanvas;
import ca.bcit.jfx.components.TaskReadyProgressBar;
import ca.bcit.net.Network;
import ca.bcit.net.NetworkLink;
import ca.bcit.net.NetworkNode;
import ca.bcit.net.demand.generator.AnycastDemandGenerator;
import ca.bcit.net.demand.generator.DemandGenerator;
import ca.bcit.net.demand.generator.TrafficGenerator;
import ca.bcit.net.demand.generator.UnicastDemandGenerator;
import ca.bcit.net.spectrum.Spectrum;
import ca.bcit.utils.Utils;
import ca.bcit.utils.random.ConstantRandomVariable;
import ca.bcit.utils.random.MappedRandomVariable;
import ca.bcit.utils.random.UniformRandomVariable;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.geometry.Insets;

import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import org.reactfx.util.FxTimer;
import org.reactfx.util.Timer;


public class MainWindowController implements Loadable {
    private static final int PROPERTIES_PANE_NUMBER = 2;
    private int i;
    @FXML
    private Console console;
    @FXML
    private TaskReadyProgressBar progressBar;
    @FXML
    private SimulationMenuController simulationMenuController;
    @FXML
    public ResizableCanvas canvas;
    @FXML
    private Accordion accordion;
    @FXML
    private Label info;
    @FXML
    private TitledPane liveInfoPane;
    @FXML
    private ImageView mapViewer;
    @FXML
    private Button updateTopologyButton;

    FileChooser fileChooser;

    File file;

    public double totalVolume;
    public double spectrumBlockedVolume;
    public double regeneratorsBlockedVolume;
    public double linkFailureBlockedVolume;

    private Timer timer;
    private static Timeline updateTimeline;
    private static ScheduledExecutorService executorService;

    public ResizableCanvas getCanvas() {
        return canvas;
    }

    public void setCanvas(ResizableCanvas canvas) {
        this.canvas = canvas;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ImageView getMapViewer() {
        return mapViewer;
    }

    public void setMapViewer(ImageView mapViewer) {
        this.mapViewer = mapViewer;
    }

    /**
     * Changes state to add Node to a map
     *
     * @deprecated currently not in use
     */
    @FXML
    private void nodeAdd(ActionEvent e) {
        canvas.changeState(DrawingState.nodeAddingState);
    }

    /**
     * Changes state to add Link to a map
     *
     * @deprecated currently not in use
     */
    @FXML
    private void linkAdd(ActionEvent e) {
        canvas.changeState(DrawingState.linkAddingState);
    }

    @FXML
    private void nodeSelect(ActionEvent e) {
        canvas.changeState(DrawingState.clickingState);
    }

    /**
     * Changes state to delete Node from a map
     *
     * @deprecated currently not in use
     */
    @FXML
    private void deleteNodeChose(ActionEvent e) {
        canvas.changeState(DrawingState.nodeDeleteState);
    }

    /**
     * Changes state to delete Link from a map
     *
     * @deprecated currently not in use
     */
    @FXML
    private void deleteLinkChose(ActionEvent e) {
        canvas.changeState(DrawingState.linkDeleteState);
    }

    /**
     * Changes state to delete multiple elements from a map
     *
     * @deprecated currently not in use
     */
    @FXML
    private void deleteFewElementsChose(ActionEvent e) {
        canvas.changeState(DrawingState.fewElementsDeleteState);
    }

    /**
     * Changes state to mark Node as Replica on a map
     *
     * @deprecated currently not in use
     */

    @FXML
    private void nodeMarkReplica(ActionEvent e) {
        canvas.changeState(DrawingState.nodeMarkReplicaState);
    }

    /**
     * Changes state to mark Node as International on a map
     *
     * @deprecated currently not in use
     */
    @FXML
    private void nodeMarkInternational(ActionEvent e) {
        canvas.changeState(DrawingState.nodeMarkInternationalState);
    }

    /**
     * Changes state to unmark Node on a map
     *
     * @deprecated currently not in use
     */
    @FXML
    private void nodeUnmark(ActionEvent e) {
        canvas.changeState(DrawingState.nodeUnmarkState);
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

        canvas.init(this);

        BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, true, false);
        RadialGradient bgGradient = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop[]{
                new Stop(0, Color.web("#004B9E")),
                new Stop(0.5, Color.web("#004B9E")),
                new Stop(1, Color.web("#003C79"))
        });
        List<BackgroundFill> bgFill = Arrays.asList(new BackgroundFill(bgGradient, CornerRadii.EMPTY, Insets.EMPTY));
        List<BackgroundImage> bg = Arrays.asList(new BackgroundImage(new Image(getClass().getResourceAsStream("/ca/bcit/jfx/res/images/LogoEON.png")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize));
        accordion.setBackground(new Background(bgFill, bg));

        simulationMenuController.setProgressBar(progressBar);
    }

    /**
     * Opens up live info tab with the node/link information
     *
     * @param fig  node or link that is selected
     * @param list list of all links & nodes
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

    @FXML
    public void createButtonClicked() throws IOException {
        String rootPath = System.getProperty("user.dir");
        Path apiKeyPath = Paths.get(rootPath + "/api_key.txt");
        GridPane grid = new GridPane();
        if (Files.exists(apiKeyPath)) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/views/SaveMapWindow.fxml"));
            grid = fxmlLoader.load();
            SaveMapController controller = fxmlLoader.getController();
            if (controller != null) {
                controller.displaySaveMapWindow();
            }
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/views/APIKeyWindow.fxml"));
            grid = fxmlLoader.load();
            APIKeyController controller = fxmlLoader.getController();
            if (controller != null) {
                controller.displaySaveAPIKeyWindow(grid);
            }
        }
    }

    @FXML
    public void updateButtonClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/views/SaveMapWindow.fxml"));
        fxmlLoader.load();
        SaveMapController controller = fxmlLoader.getController();
        controller.displaySaveMapWindow();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                controller.populateTableWithLoadedTopology();
                return null;
            }
        };
        task.run();
    }

    // live GUI updates during simulation
    public void updateGraph() {


//        Runnable updateCanvasFigures = () -> {
//            try {
//                canvas.resetCanvas();
//                Project project = ApplicationResources.getProject();
//                for (NetworkNode n : project.getNetwork().getNodes()) {
//                    n.updateRegeneratorCount();
//                    canvas.addNetworkNode(n);
//                    for (NetworkNode n2 : project.getNetwork().getNodes()) {
//                        if (project.getNetwork().containsLink(n, n2)) {
//                            NetworkLink networkLink = project.getNetwork().getLink(n, n2);
//                            Spectrum linkSpectrum = project.getNetwork().getLinkSlices(n, n2);
//                            int totalSlices = linkSpectrum.getSlicesCount();
//                            int occupiedSlices = linkSpectrum.getOccupiedSlices();
//                            int currentPercentage = (totalSlices - occupiedSlices) * 100 / totalSlices;
//                            canvas.addLink(n.getPosition(), n2.getPosition(), currentPercentage, networkLink.getLength());
//                        }
//                    }
//                }
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                System.out.println("An exception on updating the network UI");
//            }
//        };
//
//        executorService = Executors.newScheduledThreadPool(1);
//        executorService.scheduleAtFixedRate(updateCanvasFigures, 0, 500, TimeUnit.MILLISECONDS);

        updateTimeline = new Timeline(
                new KeyFrame(
                        Duration.millis(200),
                        event -> {
                            try {
                                canvas.resetCanvas();
                                Project project = ApplicationResources.getProject();
                                for (NetworkNode n : project.getNetwork().getNodes()) {
                                    n.updateRegeneratorCount();
                                    canvas.addNetworkNode(n);
                                    for (NetworkNode n2 : project.getNetwork().getNodes()) {
                                        if (project.getNetwork().containsLink(n, n2)) {
                                            NetworkLink networkLink = project.getNetwork().getLink(n, n2);
                                            Spectrum linkSpectrum = project.getNetwork().getLinkSlices(n, n2);
                                            int totalSlices = linkSpectrum.getSlicesCount();
                                            int occupiedSlices = linkSpectrum.getOccupiedSlices();
                                            int currentPercentage = (totalSlices - occupiedSlices) * 100 / totalSlices;
                                            canvas.addLink(n.getPosition(), n2.getPosition(), currentPercentage, networkLink.getLength());
                                        }
                                    }
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                System.out.println("An exception on updating the network UI");
                            }
                        }
                )
//                new KeyFrame(Duration.seconds(0.5))
        );
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();

////        Platform.runLater(updateTimeline::play);

//        timer = FxTimer.runPeriodically(
//                Duration.ofMillis(1),
//                () -> {
//                    try {
//                        canvas.resetCanvas();
//                        Project project = ApplicationResources.getProject();
//                        for (NetworkNode n : project.getNetwork().getNodes()) {
//                            n.updateRegeneratorCount();
//                            canvas.addNetworkNode(n);
//                            for (NetworkNode n2 : project.getNetwork().getNodes()) {
//                                if (project.getNetwork().containsLink(n, n2)) {
//                                    NetworkLink networkLink = project.getNetwork().getLink(n, n2);
//                                    Spectrum linkSpectrum = project.getNetwork().getLinkSlices(n, n2);
//                                    int totalSlices = linkSpectrum.getSlicesCount();
//                                    int occupiedSlices = linkSpectrum.getOccupiedSlices();
//                                    int currentPercentage = (totalSlices - occupiedSlices) * 100 / totalSlices;
//                                    canvas.addLink(n.getPosition(), n2.getPosition(), currentPercentage, networkLink.getLength());
//                                }
//                            }
//                        }
//
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                        System.out.println("An exception on updating the network UI");
//                    }
//                }
//        );

    }

    // stop GUI update
    public void stopUpdateGraph() {
        System.out.println("Timeline stopped");
//        executorService.shutdownNow();
        updateTimeline.stop();
//        timer.stop();
    }

    // reset the GUI after stop/finish
    public void resetGraph() {
        try {
            canvas.resetCanvas();
            Project project = ApplicationResources.getProject();
            for (NetworkNode n : project.getNetwork().getNodes()) {
                n.clearOccupied();
                n.setFigure(n);
                canvas.addNetworkNode(n);
                for (NetworkNode n2 : ApplicationResources.getProject().getNetwork().getNodes()) {
                    if (ApplicationResources.getProject().getNetwork().containsLink(n, n2)) {
                        NetworkLink networkLink = project.getNetwork().getLink(n, n2);
                        canvas.addLink(n.getPosition(), n2.getPosition(), 100, networkLink.getLength());
                    }
                }
            }

        } catch (Exception ex) {
            new ErrorDialog("An exception occurred while updating the project.", ex);
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    public void loadButtonClicked() {
        boolean loadSuccessful = selectFileToLoad();
        if (loadSuccessful) {
            try {
                initalizeSimulationsAndNetworks();
            } catch (MapLoadingException ex) {
                new ErrorDialog(ex.getMessage(), ex);
                ex.printStackTrace();
                return;
            } catch (Exception ex) {
                new ErrorDialog("An exception occurred while loading the project.", ex);
                ex.printStackTrace();
                return;
            }
        }
    }

    public boolean selectFileToLoad() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(ProjectFileFormat.getExtensionFilters());
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        file = fileChooser.showOpenDialog(null);
        return (file != null);
    }

    public void initalizeSimulationsAndNetworks() throws MapLoadingException, Exception {
        boolean loadSuccessful = false;

        try {
            Logger.info("Loading project from " + file.getName() + "...");
            Project project = ProjectFileFormat.getFileFormat(fileChooser.getSelectedExtensionFilter()).load(file);
            ApplicationResources.setProject(project);
            setupGenerators(project);
            loadSuccessful = true;
            canvas.resetCanvas();
            mapViewer.setImage(SwingFXUtils.toFXImage(project.getMap(), null));
            //for every node in the network place onto map and for each node add links between
            for (NetworkNode n : project.getNetwork().getNodes()) {
                n.setFigure(n);
                canvas.addNetworkNode(n);
                for (NetworkNode n2 : project.getNetwork().getNodes()) {
                    NetworkLink networkLink = project.getNetwork().getLink(n, n2);
                    if (project.getNetwork().containsLink(n, n2)) {
                        canvas.addLink(n.getPosition(), n2.getPosition(), 100, networkLink.getLength());
                    }
                }
            }
            updateTopologyButton.setDisable(false);

        } catch (MapLoadingException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (loadSuccessful) {
                Logger.info("Finished loading project.");
            } else {
                Logger.info("Loading cancelled");
            }
        }


        Task<Void> task2 = new Task<Void>() {

            @Override
            protected Void call() {
                Network network = ApplicationResources.getProject().getNetwork();

                i = 1;
                try {
                    network.setMaxPathsCount(network.calculatePaths(() -> updateProgress(i++, network.getNodesPairsCount())));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                Console.cout.println("Max best paths count: " + network.getMaxPathsCount());

                return null;
            }


        };
        SimulationMenuController.progressBar.runTask(task2, true);
    }

    @FXML
    public void whilePaused() {
        canvas.changeState(DrawingState.clickingState);
        info.setText("Blocked Spectrum: " + this.spectrumBlockedVolume / this.totalVolume * 100 + "%" + "\n"
                + "Blocked Regenerators: " + this.regeneratorsBlockedVolume / this.totalVolume * 100 + "%" + "\n"
                + "Blocked Link Failure: " + this.linkFailureBlockedVolume / this.totalVolume * 100 + "%");
    }

    /**
     * Method will setup generator behavior for no backup, dedicated backup, and shared backup options
     *
     * @param project loaded
     */
    public void setupGenerators(Project project) throws MapLoadingException {
        Network network = project.getNetwork();
        List<TrafficGenerator> generators = project.getTrafficGenerators();

        List<MappedRandomVariable.Entry<DemandGenerator<?>>> subGenerators = new ArrayList<>();

        try {
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
        } catch (NullPointerException ex) {
            throw new MapLoadingException("Topology should contain both international and data center node types");
        }
    }

}
