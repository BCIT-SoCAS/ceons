package ca.bcit;

import ca.bcit.i18n.LocaleEnum;
import ca.bcit.io.YamlSerializable;
import ca.bcit.io.project.EONProjectFileFormat;
import ca.bcit.io.project.ProjectFileFormat;
import ca.bcit.net.Network;
import ca.bcit.net.NetworkLink;
import ca.bcit.net.NetworkNode;
import ca.bcit.net.demand.generator.AnycastDemandGenerator;
import ca.bcit.net.demand.generator.TrafficGenerator;
import ca.bcit.net.demand.generator.UnicastDemandGenerator;
import ca.bcit.utils.LocaleUtils;
import ca.bcit.utils.random.ConstantRandomVariable;
import ca.bcit.utils.random.IrwinHallRandomVariable;
import ca.bcit.utils.random.MappedRandomVariable;
import ca.bcit.utils.random.UniformRandomVariable;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import com.sun.javafx.application.LauncherImpl;
import javafx.scene.image.Image;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

	private final static int SPLASH_SCREEN_TIMER = 3000;
	public static LocaleEnum CURRENT_LOCALE = LocaleEnum.EN_CA;
	private static Stage primaryStage;
	private static URL resourceUrl;
	private static InputStream iconResourceStream;

	@Override
	public void init() throws Exception {
		try {
			Thread.sleep(SPLASH_SCREEN_TIMER);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public void start(Stage stage) throws IOException {
		primaryStage = stage;
		resourceUrl = getClass().getResource("/ca/bcit/jfx/res/views/MainWindow.fxml");
		iconResourceStream = getClass().getResourceAsStream("/ca/bcit/jfx/res/images/LogoBCIT.png");
		loadView(LocaleUtils.getLocaleFromLocaleEnum(CURRENT_LOCALE));
	}

	public static void loadView(java.util.Locale locale) throws IOException {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("ca.bcit.bundles.lang", locale);
		FXMLLoader loader = new FXMLLoader(resourceUrl, resourceBundle);

		ProjectFileFormat.registerFileFormat(new EONProjectFileFormat(resourceBundle));

		GridPane root = loader.load();

		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image(iconResourceStream));
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

	public static void main(String[] args) {
		try {
			registerYamlSerializableClasses();

			LauncherImpl.launchApplication(Main.class, SplashScreen.class, args);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Fatal error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
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
