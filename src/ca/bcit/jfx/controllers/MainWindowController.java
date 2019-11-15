package ca.bcit.jfx.controllers;

import ca.bcit.Application;
import ca.bcit.ApplicationResources;
import ca.bcit.Settings;
import ca.bcit.drawing.Figure;
import ca.bcit.drawing.FigureControl;
import ca.bcit.drawing.Link;
import ca.bcit.drawing.Node;
import ca.bcit.i18n.LocaleEnum;
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
import ca.bcit.utils.LocaleUtils;
import ca.bcit.utils.Utils;
import ca.bcit.utils.random.ConstantRandomVariable;
import ca.bcit.utils.random.MappedRandomVariable;
import ca.bcit.utils.random.UniformRandomVariable;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.geometry.Insets;

import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.net.URL;
import java.util.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.concurrent.ScheduledExecutorService;

public class MainWindowController implements Initializable {
    private static final int PROPERTIES_PANE_NUMBER = 2;
    private int i;
    @FXML
    private Console console;
    @FXML
    private TaskReadyProgressBar progressBar;
    @FXML
    private SimulationMenuController simulationMenuController;
    @FXML
    public ResizableCanvas graph;
    @FXML
    public ResizableCanvas map;
    @FXML
    private Accordion accordion;
    @FXML
    private Label info;
    @FXML
    private TitledPane liveInfoPane;
    @FXML
    private Slider zoomSlider;
    @FXML
    private Button updateTopologyButton;
    @FXML
    private ComboBox<String> localeCombo;

    FileChooser fileChooser;

    File file;

    public double totalVolume;
    public double spectrumBlockedVolume;
    public double regeneratorsBlockedVolume;
    public double linkFailureBlockedVolume;
    public Image mapImage;

    private static Timeline updateTimeline;
    private static ScheduledExecutorService executorService;

    public ResizableCanvas getCanvas() {
        return graph;
    }

    public void setCanvas(ResizableCanvas canvas) {
        this.graph = canvas;
    }

    public ResizableCanvas getGraph() {
        return graph;
    }

    public void setGraph(ResizableCanvas graph) {
        this.graph = graph;
    }

    public void setFile(File file) {
        this.file = file; 
    }

    /**
     * Changes state to add Node to a map
     *
     * @deprecated currently not in use
     */
    @FXML
    private void nodeAdd(ActionEvent e) {
        graph.changeState(DrawingState.nodeAddingState);
    }

    /**
     * Changes state to add Link to a map
     *
     * @deprecated currently not in use
     */
    @FXML
    private void linkAdd(ActionEvent e) {
        graph.changeState(DrawingState.linkAddingState);
    }

    @FXML
    private void nodeSelect() {
        graph.changeState(DrawingState.clickingState);
    }

    @FXML
    private void drag() {
        graph.changeState(DrawingState.draggingState);
    }

    @FXML
    private void clearState() {
        graph.changeState(DrawingState.none);
    }

    /**
     * Changes state to delete Node from a map
     *
     * @deprecated currently not in use
     */
    @FXML
    private void deleteNodeChose(ActionEvent e) {
        graph.changeState(DrawingState.nodeDeleteState);
    }

    /**
     * Changes state to delete Link from a map
     *
     * @deprecated currently not in use
     */
    @FXML
    private void deleteLinkChose(ActionEvent e) {
        graph.changeState(DrawingState.linkDeleteState);
    }

    /**
     * Changes state to delete multiple elements from a map
     *
     * @deprecated currently not in use
     */
    @FXML
    private void deleteFewElementsChose(ActionEvent e) {
        graph.changeState(DrawingState.fewElementsDeleteState);
    }

    /**
     * Changes state to mark Node as Replica on a map
     *
     * @deprecated currently not in use
     */

    @FXML
    private void nodeMarkReplica(ActionEvent e) {
        graph.changeState(DrawingState.nodeMarkReplicaState);
    }

    /**
     * Changes state to mark Node as International on a map
     *
     * @deprecated currently not in use
     */
    @FXML
    private void nodeMarkInternational(ActionEvent e) {
        graph.changeState(DrawingState.nodeMarkInternationalState);
    }

    /**
     * Changes state to unmark Node on a map
     *
     * @deprecated currently not in use
     */
    @FXML
    private void nodeUnmark(ActionEvent e) {
        graph.changeState(DrawingState.nodeUnmarkState);
    }

    @FXML
    public void initialize(URL location, ResourceBundle resourceBundle) {
        for (Field field : MainWindowController.class.getDeclaredFields())
            if (field.isAnnotationPresent(FXML.class))
                try {
                    assert field.get(this) != null : "Id '" + field.getName() + "' was not injected!";
                }
                catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

        localeCombo.setItems(new ObservableListWrapper<>(LocaleEnum.labels()));
        localeCombo.setValue(Settings.CURRENT_LOCALE.label);
        localeCombo.getSelectionModel().selectedItemProperty().addListener(MainWindowController::localeChanged);

        try {
            Utils.setStaticFinal(Console.class, "cout", console);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        graph.init(this);

        BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, true, false);
        RadialGradient bgGradient = new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#004B9E")),
                new Stop(0.5, Color.web("#004B9E")),
                new Stop(1, Color.web("#003C79")));
        List<BackgroundFill> bgFill = Collections.singletonList(new BackgroundFill(bgGradient, CornerRadii.EMPTY, Insets.EMPTY));
        List<BackgroundImage> bg = Collections.singletonList(new BackgroundImage(new Image(getClass().getResourceAsStream("/ca/bcit/jfx/res/images/LogoEON.png")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize));
        accordion.setBackground(new Background(bgFill, bg));

        simulationMenuController.setProgressBar(progressBar);

        zoomSlider.setMin(Settings.ZOOM_MIN_LEVEL);
        zoomSlider.setMax(Settings.ZOOM_MAX_LEVEL);

        zoomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Settings.zoomLevel = (float) newValue.doubleValue();
            map.setScaleX(Settings.zoomLevel);
            map.setScaleY(Settings.zoomLevel);
            graph.setScaleX(Settings.zoomLevel);
            graph.setScaleY(Settings.zoomLevel);
        });

        graph.scaleXProperty().addListener(observable -> graph.list.redraw());
        graph.scaleYProperty().addListener(observable -> graph.list.redraw());
    }

    private static void localeChanged(ObservableValue<? extends String> selected, String oldLanguage, String newLanguage) {
        try {
            if (!newLanguage.equals(Settings.CURRENT_LOCALE.label)) {
                Settings.CURRENT_LOCALE = LocaleEnum.getEnumByString(newLanguage);
                Application.reloadInterface();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens up live info tab with the node/link information
     *
     * @param fig  node or link that is selected
     * @param list list of all links & nodes
     */
    public void loadProperties(Figure fig, FigureControl list) {
        {
            if (fig instanceof Node)
                loadNodeProperties(fig, list);
            else if (fig instanceof Link)
                loadLinkProperties(fig, list);
            else
                loadCurrentSummary(this.spectrumBlockedVolume, this.regeneratorsBlockedVolume, this.linkFailureBlockedVolume, this.totalVolume);
        }
    }

    private void loadCurrentSummary(double spectrumBlocked, double regeneratorsBlocked, double linkFailureBlocked, double totalVolume) {
        TitledPane properties = new TitledPane();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/views/LiveInfoSummary.fxml"), Settings.getCurrentResources());
        try {
            properties = (TitledPane) fxmlLoader.load();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        LiveInfoSummaryController controller = fxmlLoader.<LiveInfoSummaryController>getController();
        if (controller != null)
            controller.fillInformation(spectrumBlocked, regeneratorsBlocked, linkFailureBlocked, totalVolume);

        setLiveInfoPaneContent(properties);
        setExpandedPane(PROPERTIES_PANE_NUMBER);
    }

    private void loadNodeProperties(Figure temp, FigureControl list) {
        TitledPane properties = new TitledPane();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/views/NodeProperties.fxml"), Settings.getCurrentResources());
        try {
            properties = (TitledPane) fxmlLoader.load();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        NodePropertiesController controller = fxmlLoader.<NodePropertiesController>getController();
        if (controller != null)
            controller.initData(list, temp);

        setLiveInfoPaneContent(properties);
        setExpandedPane(PROPERTIES_PANE_NUMBER);
    }

    private void loadLinkProperties(Figure temp, FigureControl list) {
        TitledPane properties = new TitledPane();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/views/LinkProperties.fxml"), Settings.getCurrentResources());
        try {
            properties = fxmlLoader.load();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        LinkPropertiesController controller = fxmlLoader.<LinkPropertiesController>getController();
        if (controller != null)
            controller.initData(list, temp);

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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/views/SaveMapWindow.fxml"), Settings.getCurrentResources());
            fxmlLoader.load();
            SaveMapController controller = fxmlLoader.getController();
            if (controller != null)
                controller.displaySaveMapWindow();
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/views/APIKeyWindow.fxml"), Settings.getCurrentResources());
            grid = fxmlLoader.load();
            APIKeyController controller = fxmlLoader.getController();
            FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/views/SaveMapWindow.fxml"), Settings.getCurrentResources());
            fxmlLoader2.load();
            SaveMapController saveMapController = fxmlLoader2.getController();
            if (controller != null) {
                controller.displaySaveAPIKeyWindow(grid);
                saveMapController.displaySaveMapWindow();
            }
        }
    }

    @FXML
    public void updateButtonClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/views/SaveMapWindow.fxml"), Settings.getCurrentResources());
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

    public void updateGraph() {
        updateTimeline = new Timeline(
            new KeyFrame(
                Duration.millis(200),
                event -> {
                    try {
                        graph.resetCanvas();
                        Project project = ApplicationResources.getProject();
                        for (NetworkNode n : project.getNetwork().getNodes()) {
                            n.updateRegeneratorCount();
                            graph.addNetworkNode(n);
                            for (NetworkNode n2 : project.getNetwork().getNodes())
                                if (project.getNetwork().containsLink(n, n2)) {
                                    NetworkLink networkLink = project.getNetwork().getLink(n, n2);
                                    Spectrum linkSpectrum = project.getNetwork().getLinkSlices(n, n2);
                                    int totalSlices = linkSpectrum.getSlicesCount();
                                    int occupiedSlices = linkSpectrum.getOccupiedSlices();
                                    int currentPercentage = (totalSlices - occupiedSlices) * 100 / totalSlices;
                                    graph.addLink(n.getPosition(), n2.getPosition(), currentPercentage, networkLink.getLength());
                                }
                        }

                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            )
        );
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }

    public void stopUpdateGraph() {
        updateTimeline.stop();
    }

    public void resetGraph() {
        try {
            Project project = ApplicationResources.getProject();
            graph.resetCanvas();

            for (NetworkNode n : project.getNetwork().getNodes()) {
                n.clearOccupied();
                graph.addNetworkNode(n);
                for (NetworkNode n2 : ApplicationResources.getProject().getNetwork().getNodes()){
                    NetworkLink networkLink = project.getNetwork().getLink(n, n2);
                    if (project.getNetwork().containsLink(n, n2))
                        graph.addLink(n.getPosition(), n2.getPosition(), 100, networkLink.getLength());
                }
            }
            graph.list.redraw();
        }
        catch (Exception ex) {
            new ErrorDialog(LocaleUtils.translate("an_exception_occurred_while_updating_the_project"), ex);
        }
    }

    @FXML
    public void loadButtonClicked() {
        boolean loadSuccessful = selectFileToLoad();
        if (loadSuccessful)
            try {
                initializeSimulationsAndNetworks();
            }
            catch (MapLoadingException ex){
                new ErrorDialog(ex.getMessage(), ex);
                ex.printStackTrace();
            }
            catch (Exception ex){
                new ErrorDialog(LocaleUtils.translate("an_exception_occurred_while_loading_the_project"), ex);
                ex.printStackTrace();
            }
    }

    public boolean selectFileToLoad() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(ProjectFileFormat.getExtensionFilters());
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        file = fileChooser.showOpenDialog(null);
        return (file != null);
    }

    public void initializeSimulationsAndNetworks() throws MapLoadingException, Exception {
        zoomSlider.setValue(Settings.ZOOM_MIN_LEVEL);
        clearState();
        boolean loadSuccessful = false;
        try {
            Logger.info(LocaleUtils.translate("loading_project_from") + " " + file.getName() + "...");

            Project project = ProjectFileFormat.getFileFormat(fileChooser.getSelectedExtensionFilter()).load(file);
            ApplicationResources.setProject(project);
            setupGenerators(project);
            loadSuccessful = true;
            mapImage = SwingFXUtils.toFXImage(project.getMap(), null);
            map.getGraphicsContext2D().drawImage(mapImage, 0, 0, map.getWidth(), map.getHeight());
            graph.resetCanvas();

            for (NetworkNode n : project.getNetwork().getNodes()) {
                n.setFigure();
                graph.addNetworkNode(n);
                for (NetworkNode n2 : project.getNetwork().getNodes()) {
                    NetworkLink networkLink = project.getNetwork().getLink(n, n2);
                    if (project.getNetwork().containsLink(n, n2))
                        graph.addLink(n.getPosition(), n2.getPosition(), 100, networkLink.getLength());
                }
            }
            updateTopologyButton.setDisable(false);
        }
        finally {
            if (loadSuccessful)
                Logger.info(LocaleUtils.translate("finished_loading_project"));
            else
                Logger.info(LocaleUtils.translate("loading_cancelled"));
        }

        Task<Void> task2 = new Task<Void>() {

            @Override
            protected Void call() {
            Network network = ApplicationResources.getProject().getNetwork();

            i = 1;
            try {
                network.setMaxPathsCount(network.calculatePaths(() -> updateProgress(i++, network.getNodesPairsCount())));
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
            Logger.info(LocaleUtils.translate("max_best_paths_count_label") + " " + network.getMaxPathsCount());

            return null;
            }


        };
        SimulationMenuController.progressBar.runTask(task2, true);
    }

    @FXML
    public void whilePaused() {
        graph.changeState(DrawingState.clickingState);

        String blockedSpectrum = spectrumBlockedVolume / totalVolume * 100 * 100 + "%";
        String blockedRegenerators = regeneratorsBlockedVolume / totalVolume * 100 + "%";
        String blockedLinkFailure = linkFailureBlockedVolume / totalVolume * 100 + "%";

        info.setText(LocaleUtils.translate("blocked_spectrum_label") + " " + blockedSpectrum + "\n"
                + LocaleUtils.translate("blocked_regenerators_label") + " " + blockedRegenerators + "\n"
                + LocaleUtils.translate("blocked_link_failure_label") + " " + blockedLinkFailure);
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


            generators.add(new TrafficGenerator(LocaleUtils.translate("no_backup"), new MappedRandomVariable<>(subGenerators)));

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

            generators.add(new TrafficGenerator(LocaleUtils.translate("dedicated_backup"), new MappedRandomVariable<>(subGenerators)));

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

            generators.add(new TrafficGenerator(LocaleUtils.translate("shared_backup"), new MappedRandomVariable<>(subGenerators)));

            SimulationMenuController.generatorsStatic.setItems(new ObservableListWrapper<>(generators));
        }
        catch (NullPointerException ex) {
            throw new MapLoadingException(LocaleUtils.translate("topology_should_contain_both_international_and_data_center_node_types"));
        }
    }

    public void setFileChooser(FileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }
}
