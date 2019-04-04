package ca.bcit.jfx.controllers;

import ca.bcit.io.Logger;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.maps.GeoApiContext;
import com.google.maps.StaticMapsApi;
import com.google.maps.model.Size;
import com.google.maps.ImageResult;



public class APIKeyController implements Initializable {
	@FXML
	private GridPane saveAPIKeyPane;
	@FXML
	private TextField saveKeyInput;
	@FXML
	private Button saveAPIKeyBtn;
	@FXML
	private Button closeAPIKeyWindowBtn;

	public void initialize(URL location, ResourceBundle resources) {
		String path = "file:" + System.getProperty("user.dir") + "/src/ca/bcit/jfx/res/images/bg.png";
        saveAPIKeyPane.setStyle("-fx-background-image: url(\"" + path + "\"); -fx-background-size: cover;");
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

    private void saveAPIkey(ActionEvent e, TextField inputField, Stage dialogWindow) {
        String apiKey = inputField.getText();
        if (!validateAPIkey(apiKey)) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Warning");
			alert.setHeaderText("API key is invalid");
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

	public void displaySaveAPIKeyWindow(GridPane grid) {
		Stage dialogWindow = new Stage();
		dialogWindow.initModality(Modality.APPLICATION_MODAL);
		dialogWindow.setTitle("Save Google Maps API key");
		dialogWindow.getIcons().add(new Image(getClass().getResourceAsStream("/ca/bcit/jfx/res/images/LogoBCIT.png")));
		
		saveAPIKeyBtn.setOnAction(e -> saveAPIkey(e, saveKeyInput, dialogWindow));
		closeAPIKeyWindowBtn.setOnAction(e -> dialogWindow.close());
		Scene scene = new Scene(grid, 520, 300);
		dialogWindow.setScene(scene);
		dialogWindow.showAndWait();
	}
}
