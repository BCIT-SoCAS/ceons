package ca.bcit.jfx.controllers;

import ca.bcit.drawing.Figure;
import ca.bcit.drawing.FigureControl;
import ca.bcit.drawing.Link;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class LinkPropertiesController implements Initializable {
    private FigureControl list;
    private Figure actualLink;
    @FXML
    private Label nameID;
    @FXML
    private Label labelStartNode;
    @FXML
    private Label labelEndNode;
    @FXML
    private Label labelUsage;
    @FXML
    private Label lengthID;

    public void initialize(URL location, ResourceBundle resources) {
        addListenerToTextFieldName();
        addListenerToTextFieldLength();
    }

    private void addListenerToTextFieldLength() {
        lengthID.textProperty().addListener((observable, oldValue, newValue) -> {
            actualLink = list.findFigureByName(nameID.getText());
            int newLength = 0;
            try {
                newLength = Integer.parseInt(newValue);
            }
            catch(NumberFormatException ignored) {}

            if (newLength<0) {
                newLength=0;
                lengthID.setText("0");
            }
            ((Link) actualLink).setLength(newLength);
        });
    }

    private void addListenerToTextFieldName() {
        nameID.textProperty().addListener((observable, oldValue, newValue) -> {
            actualLink = list.findFigureByName(oldValue);
            if (!list.containsFigureWithName(newValue))
                actualLink.setName(newValue);
            //else
              //  textFieldName.setText(oldValue);
        });
    }

    public void initData(FigureControl figureControl, Figure figure) {
        list = figureControl;
        actualLink = figure;
        fillInformation(figure);
    }

    private void fillInformation(Figure temp) {
        nameID.setText(temp.getName());
        lengthID.setText(Integer.toString(((Link) temp).getLength()));
        String startNode=list.findNodeAtPoint(temp.getStartPoint()).getName();
        String endNode=list.findNodeAtPoint(((Link) temp).getEndPoint()).getName();
        labelStartNode.setText(startNode);
        labelEndNode.setText(endNode);
        labelUsage.setText(100- temp.getInfo() + "%");
    }

    @FXML
    private void labelEndNodeMouseClicked(MouseEvent e) { }
    @FXML
    private void labelStartNodeMouseClicked(MouseEvent e) { }
    @FXML
    private void labelUsageMouseClicked(MouseEvent e) { }
}
