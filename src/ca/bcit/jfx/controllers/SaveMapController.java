package ca.bcit.jfx.controllers;

import ca.bcit.ApplicationResources;
import ca.bcit.Settings;
import ca.bcit.io.Logger;
import ca.bcit.io.MapLoadingException;
import ca.bcit.io.create.NewTopology;
import ca.bcit.io.project.ProjectFileFormat;
import ca.bcit.io.create.SavedNodeDetails;
import ca.bcit.jfx.components.InformationDialog;
import ca.bcit.jfx.components.ResizableCanvas;
import ca.bcit.jfx.components.ErrorDialog;
import ca.bcit.net.Network;
import ca.bcit.net.NetworkNode;
import ca.bcit.utils.LocaleUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SaveMapController implements Initializable {

    private static final int BOTH_GROUP_MEMBERSHIPS = 2;
    private static final int ONE_GROUP_MEMBERSHIP = 1;
    private int i;

    private Stage saveWindow;
    private TableView<SavedNodeDetails> saveTable;
    private TextField nodeNumInput, nameInput, connNodeInput, numRegeneratorInput;
    private ComboBox<String> nodeTypeInput;
    private FileChooser fileChooser;
    private File file;

    public void initialize(URL location, ResourceBundle resources) {
    }

    private boolean getMap(String requestUrl) {
        return true;
        // logic to request a map
    }

    public void addButtonClicked() {
        int nextNumNode = getNextNodeNum();

        List<String> connectedNodes = new ArrayList<>(Arrays.asList(connNodeInput.getText().split(",")));
        connectedNodes.remove("" + nextNumNode);

        SavedNodeDetails savedNodeDetails = new SavedNodeDetails(nextNumNode, nameInput.getText(), String.join(",", connectedNodes), (numRegeneratorInput.getText().equals("") ? Settings.DEFAULT_NUMBER_OF_REGENERATORS : Integer.parseInt(numRegeneratorInput.getText())), getSelectedNodeType());

        try {
            saveTable.getItems().add(savedNodeDetails);
        }
        catch (Exception e) {
            new ErrorDialog(LocaleUtils.translate("validation_message_please_fill_in_all_the_fields"));
        }

        updateNodesConnections(savedNodeDetails, connNodeInput.getText());

        nameInput.clear();
        connNodeInput.clear();
        numRegeneratorInput.clear();
        nodeTypeInput.setValue(LocaleUtils.translate("standard"));
    }

    private void updateNodesConnections(SavedNodeDetails savedNodeDetails, String connectedNodesString) {
        int nodeNum = savedNodeDetails.getNodeNum();
        ObservableList<SavedNodeDetails> allNodeDetails = saveTable.getItems();

        List<String> connectedNodes = new ArrayList<>(Arrays.asList(connectedNodesString.split(",")));

        for (SavedNodeDetails node : allNodeDetails) {
            if (connectedNodes.contains("" + node.getNodeNum()) && node.getNodeNum() != nodeNum && !Arrays.asList(node.getConnectedNodeNum().split(",")).contains("" + nodeNum))
                node.setConnectedNodeNum(node.getConnectedNodeNum().isEmpty() ? "" + nodeNum : node.getConnectedNodeNum() + "," + nodeNum);

            if (Arrays.asList(node.getConnectedNodeNum().split(",")).contains("" + nodeNum) && !Arrays.asList(savedNodeDetails.getConnectedNodeNum().split(",")).contains("" + node.getNodeNum()))
                if (savedNodeDetails.getConnectedNodeNum().isEmpty())
                    savedNodeDetails.setConnectedNodeNum("" + node.getNodeNum());
                else
                    savedNodeDetails.setConnectedNodeNum(savedNodeDetails.getConnectedNodeNum() + "," + node.getNodeNum());
        }
    }

    /*
     *Delete selected row when the delete button is clicked
     */
    public void deleteButtonClicked() {
        ObservableList<SavedNodeDetails> nodeDetailsSelected, allNodeDetails;
        allNodeDetails = saveTable.getItems();
        nodeDetailsSelected = saveTable.getSelectionModel().getSelectedItems();
        if (nodeDetailsSelected.size() == 0) {
            new ErrorDialog(LocaleUtils.translate("validation_message_no_row_selected_to_delete"));
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
        if (loadSuccessful) {
            try {
                controller.initializeSimulationsAndNetworks();
            }
            catch (MapLoadingException ex) {
                new ErrorDialog(ex.getMessage(), ex);
                ex.printStackTrace();
                return;
            }
            catch (Exception ex) {
                new ErrorDialog(LocaleUtils.translate("an_exception_occurred_while_loading_the_project"), ex);
                ex.printStackTrace();
                return;
            }
            saveTable.getItems().clear();
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                populateTableWithLoadedTopology();
                return null;
                }
            };
            task.run();
        }
    }


    /*
     *Makes calls to google API to save a map and also calculate distances, finally writes to .eon file when clicked
     */
    public void saveButtonClicked() throws IOException {
        if (!saveTableHasAtLeastThreeRows(saveTable)) {
            new ErrorDialog(LocaleUtils.translate("please_enter_at_least_three_rows_containing_different_node_types"));
            return;
        }

        if (!internationalReplicaTypesExist(saveTable)) {
            new ErrorDialog(LocaleUtils.translate("both_data_center_and_international_nodes_must_be_present_in_the_topology"));
            return;
        }

        String rootPath = System.getProperty("user.dir");
        String apiPath = "api_key.txt";
        Path apiKeyPath = Paths.get(rootPath + "/" + apiPath);
        GridPane grid = new GridPane();

        if (Files.exists(apiKeyPath)) {
            fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(ProjectFileFormat.getExtensionFilters());
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            file = fileChooser.showSaveDialog(null);

            if (file == null)
                return;

            Task<Void> task = new Task<Void>() {

                @Override
                protected Void call() {
                    // get api key
                    String key = "";
                    try {
                        key = new String(Files.readAllBytes(Paths.get(apiPath)));
                        NewTopology newTopology = new NewTopology(key);

                        for (int i = 0; i < saveTable.getItems().size(); i++) {
                            SavedNodeDetails savedNodeDetails = saveTable.getItems().get(i);
                            newTopology.addNode(savedNodeDetails);
                        }

                        Logger.info(LocaleUtils.translate("saving_project_to") + " " + file.getName() + "...");
                        ProjectFileFormat.getFileFormat(fileChooser.getSelectedExtensionFilter()).save(file, ApplicationResources.getProject(), saveTable.getItems(), newTopology.getMap());
                        saveWindow.close();
                        MainWindowController controller = ResizableCanvas.getParentController();
                        controller.setFileChooser(fileChooser);
                        controller.setFile(file);
                        Logger.info(LocaleUtils.translate("finished_saving_project"));
                        new InformationDialog(LocaleUtils.translate("project_successfully_saved_initializing_now"));
                        controller.initializeSimulationsAndNetworks();
                    }
                    catch (Exception ex) {
                        new ErrorDialog(LocaleUtils.translate("an_exception_occurred_while_saving_the_project"), ex);
                        ex.printStackTrace();
                    }
                    return null;
                }
            };
            task.run();
        }
        else {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("ca.bcit.bundles.lang", LocaleUtils.getLocaleFromLocaleEnum(Settings.CURRENT_LOCALE));
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/bcit/jfx/res/views/APIKeyWindow.fxml"), resourceBundle);
            grid = fxmlLoader.load();
            APIKeyController controller = fxmlLoader.getController();
            if (controller != null) {
                controller.displaySaveAPIKeyWindow(grid);
                apiKeyPath = Paths.get(rootPath + "/" + apiPath);
                if (Files.exists(apiKeyPath))
                    saveButtonClicked();
            }
        }
    }

    /*
     *Used to display a table view with inputs to allow user to build a network topology
     */
    public void displaySaveMapWindow() {
        saveWindow = new Stage();
        saveWindow.initModality(Modality.APPLICATION_MODAL);
        saveWindow.setTitle(LocaleUtils.translate("save_network_topology"));
        saveWindow.getIcons().add(new Image(getClass().getResourceAsStream("/ca/bcit/jfx/res/images/LogoBCIT.png")));

        //Node Number
        TableColumn<SavedNodeDetails, String> nodeNumColumn = new TableColumn<>(LocaleUtils.translate("node_number"));

        nodeNumColumn.setMinWidth(200);
        nodeNumColumn.setCellValueFactory(new PropertyValueFactory<>("nodeNum"));

        //Location Column
        TableColumn<SavedNodeDetails, String> locationColumn = new TableColumn<>(LocaleUtils.translate("location"));
        locationColumn.setMinWidth(200);
        //use the location property of our objects
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        locationColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        locationColumn.setOnEditCommit((TableColumn.CellEditEvent<SavedNodeDetails, String> t) -> {
            t.getTableView().getItems().get(t.getTablePosition().getRow()).setLocation(t.getNewValue());
        });

        //Connected to Node Column
        TableColumn<SavedNodeDetails, String> connectedNodeNumColumn = new TableColumn<>(LocaleUtils.translate("connected_node_number"));
        connectedNodeNumColumn.setMinWidth(450);
        connectedNodeNumColumn.setCellValueFactory(new PropertyValueFactory<>("connectedNodeNum"));
        connectedNodeNumColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        connectedNodeNumColumn.setOnEditCommit((TableColumn.CellEditEvent<SavedNodeDetails, String> t) -> {
            t.getTableView().getItems().get(t.getTablePosition().getRow()).setConnectedNodeNum(t.getNewValue());
        });

        //Number of Regenerators Column
        TableColumn<SavedNodeDetails, Integer> numRegeneratorColumn = new TableColumn<>(LocaleUtils.translate("number_of_regenerators"));
        numRegeneratorColumn.setMinWidth(200);
        numRegeneratorColumn.setCellValueFactory(new PropertyValueFactory<>("numRegenerators"));
        numRegeneratorColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        numRegeneratorColumn.setOnEditCommit((TableColumn.CellEditEvent<SavedNodeDetails, Integer> t) -> {
            t.getTableView().getItems().get(t.getTablePosition().getRow()).setNumRegenerators(t.getNewValue());
        });

        String[] nodeTypes = {
            LocaleUtils.translate("standard"),
            LocaleUtils.translate("data_center"),
            LocaleUtils.translate("international"),
            LocaleUtils.translate("data_center") + ", " + LocaleUtils.translate("international")
        };

        //Node Type Column
        TableColumn<SavedNodeDetails, String> nodeTypeColumn = new TableColumn<>(LocaleUtils.translate("node_type"));
        nodeTypeColumn.setMinWidth(400);
        nodeTypeColumn.setCellValueFactory(new PropertyValueFactory<>("nodeType"));
        nodeTypeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(nodeTypes));
        nodeTypeColumn.setOnEditCommit((TableColumn.CellEditEvent<SavedNodeDetails, String> t) -> {
            t.getTableView().getItems().get(t.getTablePosition().getRow()).setNodeType(t.getNewValue());
        });

        //Inputs
        nameInput = new TextField();
        nameInput.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        nameInput.setPromptText(LocaleUtils.translate("enter_location"));

        connNodeInput = new TextField();
        connNodeInput.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        connNodeInput.setPromptText(LocaleUtils.translate("enter_connected_nodes"));

        numRegeneratorInput = new TextField();
        numRegeneratorInput.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);-fx-pref-width: 230");
        numRegeneratorInput.setPromptText(LocaleUtils.translate("number_of_regenerators_placeholder"));

        nodeTypeInput = new ComboBox<>(FXCollections.observableArrayList(nodeTypes));
        nodeTypeInput.setValue(LocaleUtils.translate("standard"));

        //Buttons
        Button addButton = new Button(LocaleUtils.translate("add"));
        addButton.setOnAction(e -> addButtonClicked());

        Button deleteButton = new Button(LocaleUtils.translate("delete"));
        deleteButton.setOnAction(e -> deleteButtonClicked());

        Button loadButton = new Button(LocaleUtils.translate("load_map"));
        loadButton.setOnAction(e -> loadButtonClicked());

        Button saveButton = new Button(LocaleUtils.translate("save_map"));
        saveButton.setOnAction(e -> {
            try {
                saveButtonClicked();
            }
            catch (IOException ex) {
                ex.printStackTrace();;
            }
        });

        HBox hBox = new HBox();
        //Insets: Padding around entire layout
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(20);
        hBox.getChildren().addAll(nameInput, connNodeInput, numRegeneratorInput, nodeTypeInput, addButton, deleteButton, saveButton, loadButton);

        saveTable = new TableView<>();
        saveTable.setItems(getSavedNodeDetails());
        saveTable.setEditable(true);
        saveTable.getColumns().addAll(nodeNumColumn, locationColumn, connectedNodeNumColumn, numRegeneratorColumn, nodeTypeColumn);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(saveTable, hBox);

        Scene scene = new Scene(vBox);
        saveWindow.setScene(scene);
        saveWindow.show();
    }

    //Get all of the node details
    private ObservableList<SavedNodeDetails> getSavedNodeDetails() {
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
//        nodeDetails.add(new SavedNodeDetails(0, "London", "1,3,19", 100, "Data Center"));
//        nodeDetails.add(new SavedNodeDetails(1, "Paris", "0,2,4,6,17", 100, "Data Center"));
//        nodeDetails.add(new SavedNodeDetails(2, "Brussels", "1,3,8", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(3, "Amsterdam", "0,2,7,20", 100, "Data Center"));
//        nodeDetails.add(new SavedNodeDetails(4, "Lyon", "1,5,18", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(5, "Zurich", "4,6,9", 100, "Data Center"));
//        nodeDetails.add(new SavedNodeDetails(6, "Strasbourg", "1,5,8", 100, "Standard"));
//        nodeDetails.add(new SavedNodeDetails(7, "Hamburg", "3,8,11", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails(8, "Frankfurt", "2,6,7,10", 100, "Data Center"));
//		nodeDetails.add(new SavedNodeDetails(9, "Milan", "5,10,12", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails(10, "Munich", "8,9,11,14", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails(11, "Berlin", "7,10,15,24,25", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails(12, "Rome", "9,13,21", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails(13, "Zagreb", "12,14,22", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails(14, "Vienna", "10,13,15", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails(15, "Prague", "11,14,23", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails(16, "Madrid", "17,18", 100, "Data Center"));
//		nodeDetails.add(new SavedNodeDetails(17, "Bordeaux", "1,16", 100, "International"));
//		nodeDetails.add(new SavedNodeDetails(18, "Barcelona, Spain", "4,16", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails(19, "Dublin", "0,20", 100, "International"));
//		nodeDetails.add(new SavedNodeDetails(20, "Glasgow", "3,19", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails(21, "Athens", "22,12", 100, "International"));
//		nodeDetails.add(new SavedNodeDetails(22, "Belgrade", "13,21,23", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails(23, "Budapest", "15,22,24", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails(24, "Warsaw", "11,23,26", 100, "Data Center"));
//		nodeDetails.add(new SavedNodeDetails(25, "Copenhagen", "11,27", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails(26, "Stockholm", "24,27", 100, "Standard"));
//		nodeDetails.add(new SavedNodeDetails(27, "Oslo", "25,26", 100, "Standard"));
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

    private int getNextNodeNum() {
        return saveTable.getItems().size();
    }

    /**
     * Update all the following row numbers by decreasing their node numbers by 1
     * Remove deleted node num from nodes with connections to the deleted nodes
     *
     * @param nodeNumDeleted reference node number used to find the row to start updating from
     */
    private void updateNodeNumsUponDelete(int nodeNumDeleted) {
        ObservableList<SavedNodeDetails> allNodeDetails = saveTable.getItems();
        for (int i = nodeNumDeleted; i < allNodeDetails.size(); i++)
            allNodeDetails.get(i).setNodeNum(i);

        for (SavedNodeDetails node : allNodeDetails) {
            String connectedNumString = node.getConnectedNodeNum();
            if (!connectedNumString.isEmpty()) {
                List<String> connectedNumStringList = new ArrayList<String>(Arrays.asList(connectedNumString.split(",")));
                ListIterator<String> it = connectedNumStringList.listIterator();
                while (it.hasNext()) {
                    String nodeNum = it.next();
                    //remove deleted node number from the connected node string and update node numbers that were greater than the deleted node number
                    if (nodeNumDeleted == Integer.parseInt(nodeNum))
                        it.remove();
                    else if (nodeNumDeleted < Integer.parseInt(nodeNum))
                        it.set(Integer.parseInt(nodeNum) - 1 + "");
                }
                node.setConnectedNodeNum(String.join(",", connectedNumStringList));
            }
        }
    }

    /**
     * Identifies checkboxes marked and sets the node type to be added accordingly; default is standard
     *
     * @return node type
     */
    private String getSelectedNodeType() {
        return nodeTypeInput.getValue();
    }

    /**
     * Will populate the table view with the currently loaded topology information
     */
    public void populateTableWithLoadedTopology() {
        HashSet<ArrayList<Integer>> uniqueLinks = new HashSet<ArrayList<Integer>>();
        try {
            Network network = ApplicationResources.getProject().getNetwork();
            saveTable.getItems().clear();
            for (NetworkNode n1 : network.getNodes()) {
                ArrayList<Integer> linkedNodeNums = new ArrayList<Integer>();
                String linkedNodesToString = "";
                String nodeTypesToString = "";
                for (NetworkNode otherNode : network.getNodes())
                    if (network.containsLink(n1, otherNode))
                        linkedNodeNums.add(otherNode.getNodeNum());

                Collections.sort(linkedNodeNums);
                if (!uniqueLinks.contains(linkedNodeNums))
                    uniqueLinks.add(linkedNodeNums);

                if (!linkedNodeNums.isEmpty()) {
                    for (int i = 0; i < linkedNodeNums.size() - 1; i++)
                        linkedNodesToString += (linkedNodeNums.get(i) + ",");

                    linkedNodesToString += linkedNodeNums.get(linkedNodeNums.size() - 1);
                }

                if (n1.getNodeGroups().size() == BOTH_GROUP_MEMBERSHIPS)
                    nodeTypesToString = LocaleUtils.translate("data_center") + ", " + LocaleUtils.translate("international");
                else if (n1.getNodeGroups().size() == ONE_GROUP_MEMBERSHIP) {
                    if (n1.getNodeGroups().containsKey("replicas"))
                        nodeTypesToString = LocaleUtils.translate("data_center");
                    else if (n1.getNodeGroups().containsKey("international"))
                        nodeTypesToString = LocaleUtils.translate("international");
                }
                else
                    nodeTypesToString = LocaleUtils.translate("standard");

                SavedNodeDetails savedNodeDetails = new SavedNodeDetails(getNextNodeNum(), n1.getLocation(), linkedNodesToString, n1.getRegeneratorsCount(), nodeTypesToString);
                saveTable.getItems().add(savedNodeDetails);
            }
        }
        catch (NullPointerException e) {
            new ErrorDialog(LocaleUtils.translate("network_not_pre_loaded_from_the_main_window_exception"));
        }
        catch (Exception e) {
            new ErrorDialog(LocaleUtils.translate("some_exception_occurred_while_trying_to_load"), e);
        }
    }

    public boolean internationalReplicaTypesExist(TableView<SavedNodeDetails> saveTable){
        boolean internationalPresent = false;
        boolean dataCenterPresent = false;

        for (SavedNodeDetails node : saveTable.getItems()) {
            if (node.getNodeType().contains(LocaleUtils.translate("international")))
                internationalPresent = true;

            if(node.getNodeType().contains(LocaleUtils.translate("data_center")))
                dataCenterPresent = true;

            if (internationalPresent && dataCenterPresent)
                break;
        }

        return (internationalPresent && dataCenterPresent);
    }

    public boolean saveTableHasAtLeastThreeRows(TableView<SavedNodeDetails> saveTable){
        return (saveTable.getItems().size() >= 3);
    }
}
