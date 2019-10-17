package ca.bcit.jfx.components;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.ResourceBundle;

/**
 * @author Derek Wong
 * @version 1.0.0
 * Wrapper class to a dialog box that displays information
 */
public class InformationDialog {
    private String informationMessage;

    /**
     * Parameterized constructor to set and display the information dialog box
     * @param informationMessage to be displayed in the context
     */
    public InformationDialog(String informationMessage, ResourceBundle resources){
        setInformationMessage(informationMessage);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(resources.getString("information_dialog"));
        alert.setHeaderText(null);
        alert.setContentText(getInformationMessage());

        alert.showAndWait();
    }

    /**
     * Getter for the information message
     * @return informationMessage
     */
    private String getInformationMessage() {
        return informationMessage;
    }

    /**
     * Setter for the information message if not null or empty
     * @param informationMessage String to be set
     */
    private void setInformationMessage(String informationMessage) {
        if (informationMessage != null && !informationMessage.isEmpty())
            this.informationMessage = informationMessage;
        else
            throw new IllegalArgumentException("Information message can't be null or empty");
    }
}
