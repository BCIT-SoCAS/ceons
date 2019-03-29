package ca.bcit.jfx.controllers;

import ca.bcit.jfx.components.SavedNodeDetails;
import ca.bcit.jfx.components.StaticMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SaveMapController {
	@FXML
	private TextField saveMapInput;
	@FXML
	private Button saveMapBtn;
	@FXML
	private Button closeMapWindowBtn;

	Stage saveWindow;
	TableView<SavedNodeDetails> saveTable;
	TextField nameInput, connNodeInput, numRegeneratorInput, nodeTypeInput;

	private void saveMap(TextField inputField, Stage dialogWindow) {
        String requestLocation = inputField.getText();
		List<String> locationList = Arrays.asList(requestLocation.split(","));

		String apiPath = "api_key.txt";
		String key = "";
		try {
			key = new String(Files.readAllBytes(Paths.get(apiPath)));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		StaticMap staticMap = new StaticMap(key);
		for(String s: locationList) {
			if(s.equals(locationList.get(0))){
				System.out.println("here");
				staticMap.setCenterPoint(s);
			}
			staticMap.addLocation(s);
		}

		ArrayList<String> locations = staticMap.getLocations();
		System.out.println(Arrays.toString(locations.toArray()));
		staticMap.generateMap();

        if (!getMap(requestLocation)) {
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

	/*
	*Add a new row of node details when the add button is clicked
	 */
	public void addButtonClicked(){
		SavedNodeDetails savedNodeDetails = new SavedNodeDetails();
		savedNodeDetails.setCityName(nameInput.getText());
		savedNodeDetails.setConnectedNodeNum(Integer.parseInt(connNodeInput.getText()));
		savedNodeDetails.setNumRegenerators(Integer.parseInt((numRegeneratorInput.getText())));
		savedNodeDetails.setNodeType(nodeTypeInput.getText());
		saveTable.getItems().add(savedNodeDetails);
		nameInput.clear();
		connNodeInput.clear();
		numRegeneratorInput.clear();
		nodeTypeInput.clear();
	}

	/*
	*Delete selected row when the delete button is clicked
	 */
	public void deleteButtonClicked(){
		ObservableList<SavedNodeDetails> nodeDetailsSelected, allNodeDetails;
		allNodeDetails = saveTable.getItems();
		nodeDetailsSelected = saveTable.getSelectionModel().getSelectedItems();
		//For the instance that appears in the entire array of objects, remove it
		nodeDetailsSelected.forEach(allNodeDetails::remove);
	}

	/*
	*Used to display a table view with inputs to allow user to build a network topology
	 */
	public void displaySaveMapWindow() {
//		Stage dialogWindow = new Stage();
//		dialogWindow.initModality(Modality.APPLICATION_MODAL);
//		dialogWindow.setTitle("Save Map");
//		dialogWindow.getIcons().add(new Image("/ca/bcit/jfx/res/images/LogoBCIT.png"));
//
//		saveMapBtn.setOnAction(e -> saveMap(saveMapInput, dialogWindow));
//		closeMapWindowBtn.setOnAction(e -> dialogWindow.close());
//
//		Scene scene = new Scene(grid, 520, 300);
//		dialogWindow.setScene(scene);
//		dialogWindow.showAndWait();

		saveWindow = new Stage();
		saveWindow.initModality(Modality.APPLICATION_MODAL);

		saveWindow.setTitle("Save Network Topology");
		saveWindow.getIcons().add(new Image("/ca/bcit/jfx/res/images/LogoBCIT.png"));

		//Name Column
		TableColumn<SavedNodeDetails, String> nameColumn = new TableColumn<>("City Name");
		nameColumn.setMinWidth(200);
		//use the cityName property of our objects
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("cityName"));

		//Connected to Node Column
		TableColumn<SavedNodeDetails, String> connectedNodeNumColumn = new TableColumn<>("Connected Node # (Separate multiple links with a comma");
		connectedNodeNumColumn.setMinWidth(450);
		connectedNodeNumColumn.setCellValueFactory(new PropertyValueFactory<>("connectedNodeNum"));

		//Number of Regenerators Column
		TableColumn<SavedNodeDetails, String> numRegeneratorColumn = new TableColumn<>("# of Regenerators");
		numRegeneratorColumn.setMinWidth(200);
		numRegeneratorColumn.setCellValueFactory(new PropertyValueFactory<>("numRegenerators"));

		//Node Type Column
		TableColumn<SavedNodeDetails, String> nodeTypeColumn = new TableColumn<>("Node Type (International/Data Center/Normal)");
		nodeTypeColumn.setMinWidth(400);
		nodeTypeColumn.setCellValueFactory(new PropertyValueFactory<>("nodeType"));

		//Inputs
		nameInput = new TextField();
		nameInput.setPromptText("Enter the name of a city");

		connNodeInput = new TextField();
		connNodeInput.setPromptText("Enter connected node(s)");

		numRegeneratorInput = new TextField();
		numRegeneratorInput.setPromptText("Enter # of regenerators");

		nodeTypeInput = new TextField();
		nodeTypeInput.setPromptText("Enter the node type");

		//Button
		Button addButton = new Button("Add");
		addButton.setOnAction(e -> addButtonClicked());
		Button deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> deleteButtonClicked());

		HBox hBox = new HBox();
		//Insets: Padding around entire layout
		hBox.setPadding(new Insets(10, 10, 10, 10));
		hBox.setSpacing(20);
		hBox.getChildren().addAll(nameInput, connNodeInput, numRegeneratorInput, nodeTypeInput, addButton, deleteButton);

		saveTable = new TableView<>();
		saveTable.setItems(getSavedNodeDeatils());
		saveTable.getColumns().addAll(nameColumn, connectedNodeNumColumn, numRegeneratorColumn, nodeTypeColumn);

		VBox vBox = new VBox();
		vBox.getChildren().addAll(saveTable, hBox);

		Scene scene = new Scene(vBox);
		saveWindow.setScene(scene);
		saveWindow.showAndWait();
	}

	//Get all of the node details
	public ObservableList<SavedNodeDetails> getSavedNodeDeatils(){
		//observable list to store java objects inside
		ObservableList<SavedNodeDetails> nodeDetails = FXCollections.observableArrayList();
		nodeDetails.add(new SavedNodeDetails("Vancouver", 1, 100, "International"));
		nodeDetails.add(new SavedNodeDetails("Edmonton", 2, 200, "Data Center"));
		nodeDetails.add(new SavedNodeDetails("Calgary", 3, 300, "Normal"));
		nodeDetails.add(new SavedNodeDetails("Saskatoon", 4, 400, "International"));
		nodeDetails.add(new SavedNodeDetails("Quebec City", 5, 500, "Normal"));
		return nodeDetails;
	}
   
}
