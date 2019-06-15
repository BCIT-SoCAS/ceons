package ca.bcit.jfx.controllers;

import ca.bcit.io.create.NewTopology;
import ca.bcit.jfx.SavedNodeDetails;
import ca.bcit.io.create.StaticMap;
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

import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
	TextField nodeNumInput, nameInput, connNodeInput, numRegeneratorInput;
	CheckBox dcCheckbox, itlCheckbox, standardCheckbox;
	FileChooser fileChooser;
	File file;

	private boolean getMap(String requestUrl) {
		return true;
		// logic to request a map
	}

	/*
	*Add a new row of node details when the add button is clicked
	 */
	public void addButtonClicked(){
		SavedNodeDetails savedNodeDetails = new SavedNodeDetails();
		
		savedNodeDetails.setNodeNum(getNextNodeNum());
		savedNodeDetails.setLocation(nameInput.getText());
		savedNodeDetails.setConnectedNodeNum((connNodeInput.getText()));
		savedNodeDetails.setNumRegenerators(Integer.parseInt((numRegeneratorInput.getText())));
		savedNodeDetails.setNodeType(getSelectedNodeType());
		saveTable.getItems().add(savedNodeDetails);
		nameInput.clear();
		connNodeInput.clear();
		numRegeneratorInput.clear();
		itlCheckbox.setSelected(false);
		dcCheckbox.setSelected(false);
		standardCheckbox.setSelected(false);
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

		// get api key
		String apiPath = "api_key.txt";
		String key = "";
		try {
			key = new String(Files.readAllBytes(Paths.get(apiPath)));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

//		StaticMap staticMap = new StaticMap(key);
		NewTopology newTopology = new NewTopology(key);

		//Will get all row objects
		System.out.println(saveTable.getItems().get(saveTable.getItems().size()-1).getNodeNum());

		//Add all fields to the list for each object
		List<List<String>> arrList=new ArrayList<>();

		//Implement data manipulation here
		for (int i = 0; i < saveTable.getItems().size() ; i++) {
			savedNodeDetails=saveTable.getItems().get(i);
			arrList.add(new ArrayList<>());
			arrList.get(i).add(savedNodeDetails.getLocation());
			arrList.get(i).add(savedNodeDetails.getNodeType());
			arrList.get(i).add(""+savedNodeDetails.getConnectedNodeNum());
			arrList.get(i).add(""+savedNodeDetails.getNumRegenerators());
//			staticMap.addLocation(savedNodeDetails.getLocation(), savedNodeDetails.getNodeNum());
			newTopology.addNode(savedNodeDetails.getLocation(), savedNodeDetails.getNodeNum());
		}

		newTopology.createTopology();
//		staticMap.generateMap(true);

		//Printing
//		for (int i = 0; i < arrList.size(); i++) {
//			for (int j = 0; j < arrList.get(i).size(); j++){
//				System.out.println(arrList.get(i).get(j));
//			}
//		}
	}

	/*
	*Used to display a table view with inputs to allow user to build a network topology
	 */
	public void displaySaveMapWindow() {
		saveWindow = new Stage();
		saveWindow.initModality(Modality.APPLICATION_MODAL);

		saveWindow.setTitle("Save Network Topology");
		saveWindow.getIcons().add(new Image(getClass().getResourceAsStream("/ca/bcit/jfx/res/images/LogoBCIT.png")));

		//Node Number
		TableColumn<SavedNodeDetails, String> nodeNumColumn = new TableColumn<>("Node Number");
		nodeNumColumn.setMinWidth(200);
		nodeNumColumn.setCellValueFactory(new PropertyValueFactory<>("nodeNum"));

		//Name Column
		TableColumn<SavedNodeDetails, String> nameColumn = new TableColumn<>("Location");
		nameColumn.setMinWidth(200);
		//use the location property of our objects
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

		//Connected to Node Column
		TableColumn<SavedNodeDetails, String> connectedNodeNumColumn = new TableColumn<>("Connected Node # (Separate multiple links with a comma)");
		connectedNodeNumColumn.setMinWidth(450);
		connectedNodeNumColumn.setCellValueFactory(new PropertyValueFactory<>("connectedNodeNum"));

		//Number of Regenerators Column
		TableColumn<SavedNodeDetails, String> numRegeneratorColumn = new TableColumn<>("# of Regenerators");
		numRegeneratorColumn.setMinWidth(200);
		numRegeneratorColumn.setCellValueFactory(new PropertyValueFactory<>("numRegenerators"));

		//Node Type Column
		TableColumn<SavedNodeDetails, String> nodeTypeColumn = new TableColumn<>("Node Type");
		nodeTypeColumn.setMinWidth(400);
		nodeTypeColumn.setCellValueFactory(new PropertyValueFactory<>("nodeType"));

		//Inputs
		nameInput = new TextField();
		nameInput.setPromptText("Enter location");

		connNodeInput = new TextField();
		connNodeInput.setPromptText("Enter connected node(s)");

		numRegeneratorInput = new TextField();
		numRegeneratorInput.setPromptText("Enter # of regenerators");

		itlCheckbox = new CheckBox("International");
		dcCheckbox = new CheckBox("Data Center");
		standardCheckbox = new CheckBox("Standard");

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
		hBox.getChildren().addAll(nameInput, connNodeInput, numRegeneratorInput, dcCheckbox, itlCheckbox, standardCheckbox, addButton, deleteButton, saveButton);

		saveTable = new TableView<>();
		saveTable.setItems(getSavedNodeDeatils());
		saveTable.getColumns().addAll(nodeNumColumn, nameColumn, connectedNodeNumColumn, numRegeneratorColumn, nodeTypeColumn);

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
		nodeDetails.add(new SavedNodeDetails("Node_1", "London", "2,3,6", 100, "International"));
		nodeDetails.add(new SavedNodeDetails("Node_2", "Paris", "1,3,5", 100, "Data Center"));
		nodeDetails.add(new SavedNodeDetails("Node_3", "Brussels", "2,3,6", 100, "International"));
		nodeDetails.add(new SavedNodeDetails("Node_4", "Amsterdam", "1,3,5", 100, "Data Center"));
		nodeDetails.add(new SavedNodeDetails("Node_5", "Lyon", "2,3,6", 100, "International"));
		nodeDetails.add(new SavedNodeDetails("Node_6", "Zurich", "1,3,5", 100, "Data Center"));
		nodeDetails.add(new SavedNodeDetails("Node_7", "Strasbourg", "1,3,5", 100, "Data Center"));
		nodeDetails.add(new SavedNodeDetails("Node_8", "Hamburg", "2,3,6", 100, "International"));
//		nodeDetails.add(new SavedNodeDetails("Node_9", "Frankfurt", "1,3,5", 100, "Data Center"));
//		nodeDetails.add(new SavedNodeDetails("Node_10", "Milan", "2,3,6", 100, "International"));
//		nodeDetails.add(new SavedNodeDetails("Node_11", "Munich", "1,3,5", 100, "Data Center"));
//		nodeDetails.add(new SavedNodeDetails("Node_12", "Berlin", "2,3,6", 100, "International"));
//		nodeDetails.add(new SavedNodeDetails("Node_13", "Rome", "1,3,5", 100, "Data Center"));
//		nodeDetails.add(new SavedNodeDetails("Node_14", "Zagreb", "2,3,6", 100, "International"));
//		nodeDetails.add(new SavedNodeDetails("Node_15", "Vienna", "1,3,5", 100, "Data Center"));
//		nodeDetails.add(new SavedNodeDetails("Node_16", "Prague", "1,3,5", 100, "Data Center"));
//		nodeDetails.add(new SavedNodeDetails("Node_17", "Madrid", "2,3,6", 100, "International"));
//		nodeDetails.add(new SavedNodeDetails("Node_18", "Bordeaux", "1,3,5", 100, "Data Center"));
//		nodeDetails.add(new SavedNodeDetails("Node_19", "Barcelona, spain", "2,3,6", 100, "International"));
//		nodeDetails.add(new SavedNodeDetails("Node_20", "Dublin", "1,3,5", 100, "Data Center"));
//		nodeDetails.add(new SavedNodeDetails("Node_21", "Glasgow", "1,3,5", 100, "Data Center"));
//		nodeDetails.add(new SavedNodeDetails("Node_22", "Athens", "2,3,6", 100, "International"));
//		nodeDetails.add(new SavedNodeDetails("Node_23", "Belgrade", "1,3,5", 100, "Data Center"));
//		nodeDetails.add(new SavedNodeDetails("Node_24", "Budapest", "1,3,5", 100, "Data Center"));
//		nodeDetails.add(new SavedNodeDetails("Node_25", "Warsaw", "2,3,6", 100, "International"));
//		nodeDetails.add(new SavedNodeDetails("Node_26", "Copenhagen", "1,3,5", 100, "Data Center"));
//		nodeDetails.add(new SavedNodeDetails("Node_27", "Stockholm", "1,3,5", 100, "Data Center"));
//		nodeDetails.add(new SavedNodeDetails("Node_28", "Oslo", "1,3,5", 100, "Data Center"));

//		nodeDetails.add(new SavedNodeDetails("Node_3", "Richmond", "1,2", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails("Node_4","Delta", "5,6", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails("Node_5","New Westminster", "2,4", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails("Node_6","Surrey", "1,4", 100, "Standard"));
		return nodeDetails;
	}

	public String getNextNodeNum(){
		String [] splitString;
		String returnedString;
		int nodeNum;
		splitString = saveTable.getItems().get(saveTable.getItems().size()-1).getNodeNum().split("_");
		nodeNum = Integer.parseInt(splitString[1])+1;
		returnedString = splitString[0]+"_"+nodeNum;
		return returnedString;
	}

	public String getSelectedNodeType(){
		boolean dcSelected = dcCheckbox.isSelected();
		boolean itlSelected = itlCheckbox.isSelected();
		boolean standardSelected = standardCheckbox.isSelected();
		if (dcSelected && !itlSelected && !standardSelected){
			return "Data Center";
		} else if (dcSelected && itlSelected && !standardSelected){
			return "Data Center, International";
		} else if (!dcSelected && itlSelected && !standardSelected){
			return "International";
		} else if (!dcSelected && !itlSelected && standardSelected){
			return "Standard";
		}
		return "Standard";
	}

}
