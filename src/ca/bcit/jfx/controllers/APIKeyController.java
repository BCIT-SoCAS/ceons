package ca.bcit.jfx.controllers;

import ca.bcit.io.Logger;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.scene.Scene;
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

public class APIKeyController implements Initializable {
	@FXML
	private GridPane saveAPIKeyPane;
	@FXML
	private TextField saveKeyInput;
	@FXML
	private Button saveAPIKeyBtn;
	@FXML
	private Button closeAPIKeyWindowBtn;

	private ResourceBundle resources;

	public void initialize(URL location, ResourceBundle resources) {
	    this.resources = resources;
		BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, false, true);
		BackgroundImage bg = new BackgroundImage(new Image(getClass().getResourceAsStream("/ca/bcit/jfx/res/images/bg.png")),
			BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, bgSize);
		saveAPIKeyPane.setBackground(new Background(bg));
	}
	 
	private boolean validateAPIkey(String key) {
        GeoApiContext context = new GeoApiContext.Builder().apiKey(key).build();
        Size mapSize = new Size(200, 200);
        try {
            StaticMapsApi.newRequest(context, mapSize).center("Vancouver").zoom(100).await();
            return true;
        }
        catch (Exception ex) {
            Logger.info(resources.getString("api_key_is_invalid"));
            return false;
        }
    }

    private void writeAPIkeyToFile(String apiKey, File file) {
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.println(apiKey);
            writer.close();
        }
        catch (IOException ex) {
            Logger.info(resources.getString("an_exception_occurred_while_saving_api_key"));
            Logger.debug(ex);
        }
    }

    private void saveAPIkey(ActionEvent e, TextField inputField, Stage dialogWindow) {
        String apiKey = inputField.getText();
        if (!validateAPIkey(apiKey)) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle(resources.getString("warning"));
			alert.setHeaderText(resources.getString("api_key_is_invalid"));
            alert.showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("api_key");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(resources.getString("api_key_file_type_descriptor"), "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        final File file = fileChooser.showSaveDialog(null);

        if (file == null)
            return;

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                Logger.info(resources.getString("saving_api_key_to") + " " + file.getName() + " " + resources.getString("file"));
                writeAPIkeyToFile(apiKey, file);
                Logger.info(resources.getString("finished_saving_api_key"));
                dialogWindow.close();
                return null;
            }
        };
        task.run();
	}

	public void displaySaveAPIKeyWindow(GridPane grid) {
		Stage dialogWindow = new Stage();
		dialogWindow.initModality(Modality.APPLICATION_MODAL);
		dialogWindow.setTitle(resources.getString("save_google_maps_api_key"));
		dialogWindow.getIcons().add(new Image(getClass().getResourceAsStream("/ca/bcit/jfx/res/images/LogoBCIT.png")));
		
		saveAPIKeyBtn.setOnAction(e -> saveAPIkey(e, saveKeyInput, dialogWindow));
		closeAPIKeyWindowBtn.setOnAction(e -> dialogWindow.close());
		Scene scene = new Scene(grid, 520, 300);
		dialogWindow.setScene(scene);
		dialogWindow.showAndWait();
	}
}
