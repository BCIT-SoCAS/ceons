package ca.bcit;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.stage.StageStyle;

public class SplashScreen extends Preloader {

	private Stage preloaderStage;
	private Scene scene;
	
	@Override
	public void init() throws Exception {

		// If preloader has complex UI it's initialization can be done in SplashScreen#init
		Platform.runLater(() -> {
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(SplashScreen.class.getResource("/ca/bcit/jfx/res/SplashScreen.fxml"));
				
				AnchorPane root = (AnchorPane)loader.load();
				scene = new Scene(root);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.preloaderStage = primaryStage;

		// prevent splash screen from resizing and remove window buttons
		preloaderStage.setResizable(false);
		preloaderStage.initStyle(StageStyle.UNDECORATED);

		preloaderStage.setScene(scene);
		preloaderStage.show();
	}

	@Override
	public void handleStateChangeNotification(StateChangeNotification info) {
		// Handle state change notifications
		StateChangeNotification.Type type = info.getType();
		if (type == Type.BEFORE_START) {
			preloaderStage.hide();
		 }
	}
}