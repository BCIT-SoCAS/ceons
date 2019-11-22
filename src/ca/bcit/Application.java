package ca.bcit;

import ca.bcit.io.YamlSerializable;
import ca.bcit.io.project.EONProjectFileFormat;
import ca.bcit.io.project.ProjectFileFormat;
import ca.bcit.net.Network;
import ca.bcit.net.NetworkLink;
import ca.bcit.net.NetworkNode;
import ca.bcit.net.demand.generator.AnycastDemandGenerator;
import ca.bcit.net.demand.generator.TrafficGenerator;
import ca.bcit.net.demand.generator.UnicastDemandGenerator;
import ca.bcit.utils.random.ConstantRandomVariable;
import ca.bcit.utils.random.IrwinHallRandomVariable;
import ca.bcit.utils.random.MappedRandomVariable;
import ca.bcit.utils.random.UniformRandomVariable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class Application extends javafx.application.Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        registerAlgorithms();

        registerYamlSerializableClasses();

        loadInterface();
    }

    @Override
    public void init() {
        try {
            Thread.sleep(Settings.SPLASH_SCREEN_TIMER);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void reloadInterface() throws IOException {
        loadInterface();
    }

    public static void loadInterface() throws IOException {
        ResourceBundle currentResources = Settings.getCurrentResources();
        FXMLLoader loader = new FXMLLoader(Settings.mainWindowResourceUrl, currentResources);

        ProjectFileFormat.registerFileFormat(new EONProjectFileFormat());

        GridPane root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(Settings.bcitLogo);
        primaryStage.show();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setResizable(false);

        final Canvas graph = (Canvas) scene.lookup("#graph");
        final Canvas map = ((Canvas) scene.lookup("#map"));

        BorderPane pane = (BorderPane) scene.lookup("#borderPane");
        graph.widthProperty().bind(pane.widthProperty());
        graph.heightProperty().bind(pane.heightProperty());
        map.widthProperty().bind(pane.widthProperty());
        map.heightProperty().bind(pane.heightProperty());
    }

    private static void registerAlgorithms() throws Exception {
        Settings.registerAlgorithm("ca.bcit.net.algo.SPF");
        Settings.registerAlgorithm("ca.bcit.net.algo.AMRA");
    }

    private static void registerYamlSerializableClasses() throws NoSuchMethodException {
        YamlSerializable.registerSerializableClass(NetworkNode.class);
        YamlSerializable.registerSerializableClass(NetworkLink.class);
        YamlSerializable.registerSerializableClass(Network.class);

        YamlSerializable.registerSerializableClass(MappedRandomVariable.class);
        YamlSerializable.registerSerializableClass(UniformRandomVariable.Generic.class);
        YamlSerializable.registerSerializableClass(ConstantRandomVariable.class);
        YamlSerializable.registerSerializableClass(IrwinHallRandomVariable.Integer.class);
        YamlSerializable.registerSerializableClass(UnicastDemandGenerator.class);
        YamlSerializable.registerSerializableClass(AnycastDemandGenerator.class);
        YamlSerializable.registerSerializableClass(TrafficGenerator.class);
    }
}
