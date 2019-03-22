package ca.bcit.jfx.controllers;

import ca.bcit.jfx.StaticMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;


public class SaveMapController implements Initializable {
	@FXML
	private TextField saveMapInput;
	@FXML
	private Button saveMapBtn;
	@FXML
	private Button closeMapWindowBtn;

	public void initialize(URL location, ResourceBundle resources) {
        
	} 
	
	private void saveMap(ActionEvent e, TextField inputField, Stage dialogWindow) {
        String requestUrl = inputField.getText();
		StaticMap staticMap = new StaticMap("AIzaSyAj9PoX7gLtIJyhpeMH3X3FlkUj1RMwXFg");
		staticMap.addLocation("vancouver");
		staticMap.addLocation("burnaby");
		staticMap.addLocation("west van");
		ArrayList<String> locations = staticMap.getLocations();
		System.out.println(Arrays.toString(locations.toArray()));
		staticMap.generateMap();
        if (!getMap(requestUrl)) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Warning");
			alert.setHeaderText("Provided url is invalid");
            alert.showAndWait();
            return;
        }
	}

	private boolean getMap(String requestUrl) {
		return true;
		// logic to request a map
	}
	
	public void displaySaveMapWindow(GridPane grid) {
		Stage dialogWindow = new Stage();
		dialogWindow.initModality(Modality.APPLICATION_MODAL);
		dialogWindow.setTitle("Save Map");
		dialogWindow.getIcons().add(new Image("/ca/bcit/jfx/res/images/LogoBCIT.png"));

		saveMapBtn.setOnAction(e -> saveMap(e, saveMapInput, dialogWindow));
		closeMapWindowBtn.setOnAction(e -> dialogWindow.close());

		Scene scene = new Scene(grid, 520, 300);
		dialogWindow.setScene(scene);
		dialogWindow.showAndWait();
	}
	
   
}
