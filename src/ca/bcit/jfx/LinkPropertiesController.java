package ca.bcit.jfx;

import ca.bcit.drawing.Figure;
import ca.bcit.drawing.FigureControl;
import ca.bcit.drawing.Link;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Admin on 2015-02-03.
 */
public class LinkPropertiesController implements Initializable {
    private FigureControl list;
    private Figure actualLink;
    @FXML
    private TextField textFieldName;
    @FXML
    private Label labelStartNode;
    @FXML
    private Label labelEndNode;
    @FXML
    private TextField textFieldLength;
    public void initialize(URL location, ResourceBundle resources) {
        addListenerToTextFieldName();
        addListenerToTextFieldLength();
    }

    private void addListenerToTextFieldLength() {
        textFieldLength.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                actualLink = list.findFigureByName(textFieldName.getText());
                int newLength=0;
                try {
                    newLength = Integer.parseInt(newValue);
                }catch(NumberFormatException e)
                {
                }
                if (newLength<0) {
                    newLength=0;
                    textFieldLength.setText("0");
                }
                ((Link) actualLink).setLength(newLength);
            }
        });
    }

    private void addListenerToTextFieldName() {
        textFieldName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                actualLink = list.findFigureByName(oldValue);
                if (!list.containsFigureWithName(newValue)) {
                    actualLink.setName(newValue);
                }
                //else
                  //  textFieldName.setText(oldValue);
            }
        });
    }

    public void initDate(FigureControl _list,Figure _actualLink )
    {
        list=_list;
        actualLink =_actualLink;
        fillInformations(_actualLink);
    }

    private void fillInformations(Figure temp) {
        textFieldName.setText(temp.getName());
        textFieldLength.setText(Integer.toString(((Link) temp).getLength()));
        String startNode=list.findNodeAtPoint(temp.getStartPoint()).getName();
        String endNode=list.findNodeAtPoint(((Link) temp).getEndPoint()).getName();
        labelStartNode.setText(startNode);
        labelEndNode.setText(endNode);
    }
    @FXML
    private void labelEndNodeMouseClicked(MouseEvent e)
    {

    }
    @FXML
    private void labelStartNodeMouseClicked(MouseEvent e)
    {

    }

}
