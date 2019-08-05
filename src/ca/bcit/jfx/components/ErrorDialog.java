package ca.bcit.jfx.components;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Derek Wong
 * @version 1.0.0
 * Wrapper class to an dialog box that displays errors
 */
public class ErrorDialog {
    private String errorMessage;

    /**
     * Parameterized constructor to set and display the error dialog box
     * @param errorMessage to be displayed in the context
     */
    public ErrorDialog(String errorMessage){
        setErrorMessage(errorMessage);
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Known Error!");
        alert.setContentText(getErrorMessage());
        alert.setResizable(true);
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }

    /**
     * Overloaded Parameterized constructor to set and display the error dialog box, includes stack trace of exception
     * @param errorMessage to be displayed in the context
     * @param ex exception to be shown along with the stack trace
     */
    public ErrorDialog(String errorMessage, Exception ex){
        setErrorMessage(errorMessage);
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Unhandled Exception Error!");
        alert.setContentText(errorMessage);
        alert.setResizable(true);
        alert.getDialogPane().setMinWidth(500);

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(800);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    /**
     * Getter for the error message
     * @return errorMessage
     */
    private String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Setter for the error message if not null or empty
     * @param errorMessage String to be set
     */
    private void setErrorMessage(String errorMessage) {
        if(errorMessage != null && !errorMessage.isEmpty()){
            this.errorMessage = errorMessage;
        } else {
            throw new IllegalArgumentException("Error message can't be null or empty");
        }
    }


}
