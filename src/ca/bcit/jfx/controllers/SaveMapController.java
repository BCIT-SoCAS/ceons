package ca.bcit.jfx.controllers;

import ca.bcit.ApplicationResources;
import ca.bcit.io.Logger;
import ca.bcit.io.create.NewTopology;
import ca.bcit.io.project.Project;
import ca.bcit.io.project.ProjectFileFormat;
import ca.bcit.io.create.SavedNodeDetails;
import ca.bcit.jfx.components.Console;
import ca.bcit.jfx.components.ResizableCanvas;
import ca.bcit.net.Network;
import ca.bcit.net.NetworkLink;
import ca.bcit.net.NetworkNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
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
import java.util.*;


public class SaveMapController implements Loadable {
    private int i;
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
    public void addButtonClicked() {
        try {
            SavedNodeDetails savedNodeDetails = new SavedNodeDetails(getNextNodeNum(), nameInput.getText(), connNodeInput.getText(), Integer.parseInt(numRegeneratorInput.getText()), getSelectedNodeType());
            saveTable.getItems().add(savedNodeDetails);
        } catch (Exception e) {
            Logger.info("Please fill in all the fields");
        }

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
    public void deleteButtonClicked() {
        ObservableList<SavedNodeDetails> nodeDetailsSelected, allNodeDetails;
        allNodeDetails = saveTable.getItems();
        nodeDetailsSelected = saveTable.getSelectionModel().getSelectedItems();
        if(nodeDetailsSelected.size() == 0) {
            Logger.info("No row selected to delete");
            return;
        }
        int nodeNumOfSelected = saveTable.getSelectionModel().getSelectedItem().getNodeNum();

        //For the instance that appears in the entire array of objects, remove it
        nodeDetailsSelected.forEach(allNodeDetails::remove);
        updateNodeNumsUponDelete(nodeNumOfSelected);
    }

    /*
     * Will populate table from the loaded YAML file and load main window with the topology
     */
    public void loadButtonClicked() {
        MainWindowController controller = ResizableCanvas.getParentController();
        boolean loadSuccessful = controller.selectFileToLoad();
        if(loadSuccessful){
            controller.initalizeSimulationsAndNetworks();
            HashSet<ArrayList<Integer>> uniqueLinks = new HashSet<ArrayList<Integer>>();
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    try {
                        Network network = ApplicationResources.getProject().getNetwork();
                        for (NetworkNode n1 : network.getNodes()) {
                            ArrayList<Integer> linkedNodeNums = new ArrayList<Integer>();
                            String linkedNodesToString = "";
                            String nodeTypesToString = "";
                            for (NetworkNode otherNode : network.getNodes()) {
                                if (network.containsLink(n1, otherNode)) {
                                    linkedNodeNums.add(otherNode.getNodeNum());
                                }
                            }
                            Collections.sort(linkedNodeNums);
                            if (!uniqueLinks.contains(linkedNodeNums)) {
                                uniqueLinks.add(linkedNodeNums);
                            }

                            if (!linkedNodeNums.isEmpty()) {
                                for (int i = 0; i < linkedNodeNums.size() - 1; i++) {
                                    linkedNodesToString += (linkedNodeNums.get(i) + ",");
                                }
                                linkedNodesToString += linkedNodeNums.get(linkedNodeNums.size() - 1);
                            }
                            if (n1.getNodeGroups().size() == 2) {
                                nodeTypesToString = "Data Center, International";
                            } else if (n1.getNodeGroups().size() == 1) {
                                if (n1.getNodeGroups().containsKey("replicas")) {
                                    nodeTypesToString = "Data Center";
                                } else if (n1.getNodeGroups().containsKey("international")) {
                                    nodeTypesToString = "International";
                                }
                            } else {
                                nodeTypesToString = "Standard";
                            }
                            SavedNodeDetails savedNodeDetails = new SavedNodeDetails(getNextNodeNum(), n1.getLocation(), linkedNodesToString, n1.getRegeneratorsCount(), nodeTypesToString);
                            saveTable.getItems().add(savedNodeDetails);
                        }
                    } catch (Exception e) {
                        Logger.info("Some exception: " + e);
                    }
                    return null;
                }
            };
            task.run();
        }
    }


    /*
     *Makes calls to google API to save a map and also calculate distances, finally writes to .eon file when clicked
     */
    public void saveButtonClicked() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(ProjectFileFormat.getExtensionFilters());
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        file = fileChooser.showSaveDialog(null);

        if (file == null) return;
        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() {
                // get api key
                String apiPath = "api_key.txt";
                String key = "";
                try {
                    key = new String(Files.readAllBytes(Paths.get(apiPath)));
                    NewTopology newTopology = new NewTopology(key);

                    for (int i = 0; i < saveTable.getItems().size(); i++) {
                        SavedNodeDetails savedNodeDetails = saveTable.getItems().get(i);
                        newTopology.addNode(savedNodeDetails);
                    }

                    Logger.info("Saving project to " + file.getName() + "...");
                    ProjectFileFormat.getFileFormat(fileChooser.getSelectedExtensionFilter()).save(file, ApplicationResources.getProject(), saveTable.getItems(), newTopology.getMap(), key);
                    Logger.info("Finished saving project.");
                } catch (Exception ex) {
                    Logger.info("An exception occurred while saving the project.");
                    Logger.debug(ex);
                }
                return null;
            }
        };
        task.run();
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

        Button loadButton = new Button("Load Map");
        loadButton.setOnAction(e -> loadButtonClicked());

        Button saveButton = new Button("Save Map");
        saveButton.setOnAction(e -> saveButtonClicked());

        HBox hBox = new HBox();
        //Insets: Padding around entire layout
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(20);
        hBox.getChildren().addAll(nameInput, connNodeInput, numRegeneratorInput, dcCheckbox, itlCheckbox, standardCheckbox, addButton, deleteButton, saveButton, loadButton);

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
    public ObservableList<SavedNodeDetails> getSavedNodeDeatils() {
        //observable list to store java objects inside
        ObservableList<SavedNodeDetails> nodeDetails = FXCollections.observableArrayList();

        /**
         * Mock data for the table view
         */
        // --dt14-------------------------------------------------------------------------------------------------------------------------------
//        nodeDetails.add(new SavedNodeDetails(0, "Hannover", "13", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(1, "Frankfurt", "0,12,13,7", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(2, "Hamburg", "0", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(3, "Bremen", "2,0", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(4, "Berlin", "2,0,13", 100, "International"));
//        nodeDetails.add(new SavedNodeDetails(5, "Muenchen", "6,7", 100, "Data Center"));
//        nodeDetails.add(new SavedNodeDetails(6, "Ulm, Germany", "", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(7, "Nuernberg", "8", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(8, "Stuttgart", "6", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(9, "Essen, Germany", "", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(10, "Dortmund", "9,0,12", 100, "Data Center"));
//        nodeDetails.add(new SavedNodeDetails(11, "Duesseldorf", "9,12", 100, "International"));
//        nodeDetails.add(new SavedNodeDetails(12, "Koeln", "", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(13, "Leipzig", "7", 100, "Data Center"));
        // --dt14-------------------------------------------------------------------------------------------------------------------------------

        // --euro28-------------------------------------------------------------------------------------------------------------------------------
        nodeDetails.add(new SavedNodeDetails(0, "London", "1,3,19", 100, "Data Center"));
        nodeDetails.add(new SavedNodeDetails(1, "Paris", "0,2,4,6,17", 100, "Data Center"));
        nodeDetails.add(new SavedNodeDetails(2, "Brussels", "1,3,8", 100, "Standard"));
        nodeDetails.add(new SavedNodeDetails(3, "Amsterdam", "0,2,7,20", 100, "Data Center"));
        nodeDetails.add(new SavedNodeDetails(4, "Lyon", "1,5,18", 100, "Standard"));
        nodeDetails.add(new SavedNodeDetails(5, "Zurich", "4,6,9", 100, "Data Center"));
        nodeDetails.add(new SavedNodeDetails(6, "Strasbourg", "1,5,8", 100, "Standard"));
        nodeDetails.add(new SavedNodeDetails(7, "Hamburg", "3,8,11", 100, "Standard"));
		nodeDetails.add(new SavedNodeDetails(8, "Frankfurt", "2,6,7,10", 100, "Data Center"));
		nodeDetails.add(new SavedNodeDetails(9, "Milan", "5,10,12", 100, "Standard"));
		nodeDetails.add(new SavedNodeDetails(10, "Munich", "8,9,11,14", 100, "Standard"));
		nodeDetails.add(new SavedNodeDetails(11, "Berlin", "7,10,15,24,25", 100, "Standard"));
		nodeDetails.add(new SavedNodeDetails(12, "Rome", "9,13,21", 100, "Standard"));
		nodeDetails.add(new SavedNodeDetails(13, "Zagreb", "12,14,22", 100, "Standard"));
		nodeDetails.add(new SavedNodeDetails(14, "Vienna", "10,13,15", 100, "Standard"));
		nodeDetails.add(new SavedNodeDetails(15, "Prague", "11,14,23", 100, "Standard"));
		nodeDetails.add(new SavedNodeDetails(16, "Madrid", "17,18", 100, "Data Center"));
		nodeDetails.add(new SavedNodeDetails(17, "Bordeaux", "1,16", 100, "International"));
		nodeDetails.add(new SavedNodeDetails(18, "Barcelona, Spain", "4,16", 100, "Standard"));
		nodeDetails.add(new SavedNodeDetails(19, "Dublin", "0,20", 100, "International"));
		nodeDetails.add(new SavedNodeDetails(20, "Glasgow", "3,19", 100, "Standard"));
		nodeDetails.add(new SavedNodeDetails(21, "Athens", "22,12", 100, "International"));
		nodeDetails.add(new SavedNodeDetails(22, "Belgrade", "13,21,23", 100, "Standard"));
		nodeDetails.add(new SavedNodeDetails(23, "Budapest", "15,22,24", 100, "Standard"));
		nodeDetails.add(new SavedNodeDetails(24, "Warsaw", "11,23,26", 100, "Data Center"));
		nodeDetails.add(new SavedNodeDetails(25, "Copenhagen", "11,27", 100, "Standard"));
		nodeDetails.add(new SavedNodeDetails(26, "Stockholm", "24,27", 100, "Standard"));
		nodeDetails.add(new SavedNodeDetails(27, "Oslo", "25,26", 100, "Standard"));
        // --euro28-------------------------------------------------------------------------------------------------------------------------------

        // --us26-------------------------------------------------------------------------------------------------------------------------------
//        nodeDetails.add(new SavedNodeDetails(0, "Seattle", "2,4", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(1, "Los Angeles", "2,3,5", 100, "Data Center"));
//        nodeDetails.add(new SavedNodeDetails(2, "San Francisco", "0,1,4", 100, "Data Center, International"));
//        nodeDetails.add(new SavedNodeDetails(3, "Las Vegas", "1,4,5", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(4, "Salt Lake City", "0,2,3,11", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(5, "El Paso", "1,3,6,7", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(6, "Dallas", "5,7,8,11,16", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(7, "Houston", "5,6,21", 100, "Data Center"));
//        nodeDetails.add(new SavedNodeDetails(8, "Tulsa", "6,10,15", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(9, "Minneapolis", "10,12", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(10, "Kansas City", "8,9,11,15", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(11, "Denver", "4,6,10", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(12, "Chicago", "9,13,14,15", 100, "Data Center"));
//        nodeDetails.add(new SavedNodeDetails(13, "Indianapolis", "12,15,16,17", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(14, "Detroit", "12,17", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(15, "St Louis", "8,10,12,13", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(16, "Nashville", "6,13,20,23", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(17, "Cleveland", "13,14,19,25", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(18, "New York", "19,22,25", 100, "International"));
//        nodeDetails.add(new SavedNodeDetails(19, "Albany", "17,18,22", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(20, "Charlotte", "16,23,25", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(21, "New Orleans", "7,23,24", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(22, "Boston", "18,19", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(23, "Atlanta", "16,20,21,24", 100, "Data Center"));
//        nodeDetails.add(new SavedNodeDetails(24, "Miami", "21,23", 100, "International"));
//        nodeDetails.add(new SavedNodeDetails(25, "Washington, D.C", "17,18,20", 100, "Data Center"));
        // --us26-------------------------------------------------------------------------------------------------------------------------------

        return nodeDetails;
    }

    /**
     * Method will get return the size as the next node num because node num default is 0
     *
     * @return String representation of next node number to be placed
     */
    private int getNextNodeNum() {
        return saveTable.getItems().size();
    }

    /**
     * Update all the following row numbers by decreasing their node numbers by 1
     *
     * @param nodeNumDeleted reference node number used to find the row to start updating from
     */
    private void updateNodeNumsUponDelete(int nodeNumDeleted) {
        ObservableList<SavedNodeDetails> allNodeDetails;
        allNodeDetails = saveTable.getItems();
        for (int i = nodeNumDeleted; i < allNodeDetails.size(); i++) {
            allNodeDetails.get(i).setNodeNum(i);
        }
    }

    /**
     * Identifies checkboxes marked and sets the node type to be added accordingly; default is standard
     *
     * @return node type
     */
    private String getSelectedNodeType() {
        boolean dcSelected = dcCheckbox.isSelected();
        boolean itlSelected = itlCheckbox.isSelected();
        boolean standardSelected = standardCheckbox.isSelected();
        if (dcSelected && !itlSelected && !standardSelected) {
            return "Data Center";
        } else if (dcSelected && itlSelected && !standardSelected) {
            return "Data Center, International";
        } else if (!dcSelected && itlSelected && !standardSelected) {
            return "International";
        } else if (!dcSelected && !itlSelected && standardSelected) {
            return "Standard";
        }
        return "Standard";
    }

}
