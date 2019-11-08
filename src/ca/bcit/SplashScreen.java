package ca.bcit;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.stage.StageStyle;

public class SplashScreen extends Preloader {
	private Stage preloaderStage;
	private Scene scene;
	
	@Override
	public void init() {
		Platform.runLater(() -> {
			try {
				FXMLLoader loader = new FXMLLoader(Settings.splashScreenResourceUrl);

				AnchorPane root = loader.load();
				scene = new Scene(root);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void start(Stage primaryStage) {
		this.preloaderStage = primaryStage;

		preloaderStage.setResizable(false);
		preloaderStage.initStyle(StageStyle.UNDECORATED);
		preloaderStage.setScene(scene);
		preloaderStage.show();
	}

	@Override
	public void handleStateChangeNotification(StateChangeNotification info) {
		if (info.getType() == Type.BEFORE_START)
			preloaderStage.hide();
	}
}