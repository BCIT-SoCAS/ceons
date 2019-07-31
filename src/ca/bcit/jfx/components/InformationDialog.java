package ca.bcit.jfx.components;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class InformationDialog {
    private String informationMessage;

    public InformationDialog(String informationMessage){
        setInformationMessage(informationMessage);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(getInformationMessage());

        alert.showAndWait();
    }

    private String getInformationMessage() {
        return informationMessage;
    }

    private void setInformationMessage(String informationMessage) {
        if(informationMessage != null && !informationMessage.isEmpty()){
            this.informationMessage = informationMessage;
        } else {
            throw new IllegalArgumentException("Information message can't be null or empty");
        }
    }


}
