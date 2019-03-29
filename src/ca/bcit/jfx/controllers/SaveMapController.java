package ca.bcit.jfx.controllers;

import ca.bcit.jfx.SavedNodeDetails;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ca.bcit.jfx.NewTopology;
import javafx.fxml.FXML;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.File;
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
	FileChooser fileChooser;
	File file;


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

		NewTopology staticMap = new NewTopology(key);
		for(String s: locationList) {
			if(s.equals(locationList.get(0))){
				System.out.println("here");
			}
			staticMap.addLocation(s);
		}

		NewTopology.distance(staticMap.getLocationsLatLng().get(0).lat, staticMap.getLocationsLatLng().get(1).lat,
				staticMap.getLocationsLatLng().get(0).lng, staticMap.getLocationsLatLng().get(1).lng);

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
	*Makes calls to google API to save a map and also calculate distances, finally writes to .eon file when clicked
	 */
	public void saveButtonClicked(){
		SavedNodeDetails savedNodeDetails = new SavedNodeDetails();

		//Will get all row objects
		System.out.println(saveTable.getItems());

		//Add all fields to the list for each object
		List<List<String>> arrList=new ArrayList<>();

		//Implement data manipulation here
		for (int i = 0; i < saveTable.getItems().size() ; i++) {
			savedNodeDetails=saveTable.getItems().get(i);
			arrList.add(new ArrayList<>());
			arrList.get(i).add(savedNodeDetails.getCityName());
			arrList.get(i).add(savedNodeDetails.getNodeType());
			arrList.get(i).add(""+savedNodeDetails.getConnectedNodeNum());
			arrList.get(i).add(""+savedNodeDetails.getNumRegenerators());
		}

		//Printing
		for (int i = 0; i < arrList.size(); i++) {
			for (int j = 0; j < arrList.get(i).size(); j++){
				System.out.println(arrList.get(i).get(j));
			}
		}

//		fileChooser = new FileChooser();
//		fileChooser.getExtensionFilters().addAll(ProjectFileFormat.getExtensionFilters());
//		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
//		file = fileChooser.showSaveDialog(null);
	}

	/*
	*Used to display a table view with inputs to allow user to build a network topology
	 */
	public void displaySaveMapWindow() {
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
		TableColumn<SavedNodeDetails, String> connectedNodeNumColumn = new TableColumn<>("Connected Node # (Separate multiple links with a comma)");
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

		//Buttons
		Button addButton = new Button("Add");
		addButton.setOnAction(e -> addButtonClicked());

		Button deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> deleteButtonClicked());

		Button saveButton = new Button("Save Map");
		saveButton.setOnAction(e -> saveButtonClicked());

		HBox hBox = new HBox();
		//Insets: Padding around entire layout
		hBox.setPadding(new Insets(10, 10, 10, 10));
		hBox.setSpacing(20);
		hBox.getChildren().addAll(nameInput, connNodeInput, numRegeneratorInput, nodeTypeInput, addButton, deleteButton, saveButton);

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
