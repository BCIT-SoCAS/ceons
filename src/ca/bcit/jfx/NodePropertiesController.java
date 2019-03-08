package ca.bcit.jfx;

import ca.bcit.drawing.Figure;
import ca.bcit.drawing.FigureControl;
import ca.bcit.drawing.Node;
import ca.bcit.utils.geom.Vector2F;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Admin on 2015-02-02.
 */
public class NodePropertiesController implements Initializable {
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldRegenNum;
    @FXML
    private ListView<String> listView;
    @FXML
    private TextField textFieldX;
    @FXML
    private TextField textFieldY;
    @FXML
    private TitledPane titledPane;
    private FigureControl list;
    private Figure actualNode;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null) {
                list.setSelectedFigure(list.findFigureByName(newValue));
            }
        });
        textFieldName.textProperty().addListener((observable, oldValue, newValue) -> {
            actualNode = list.findFigureByName(oldValue);
            if (!list.containsFigureWithName(newValue)) {
                actualNode.setName(newValue);
            }
        });
    }
    @FXML
    private void listViewMouseClicked(MouseEvent e)
    {
        if(e.getClickCount()==2)
        {
            String selectedNode=listView.getSelectionModel().getSelectedItem();
            Figure node= list.findFigureByName(selectedNode);
            fillInformations(node);
            listView.getSelectionModel().clearSelection();
        }
    }
    public void initDate(FigureControl _list, Figure _node) {
        list = _list;
        actualNode = _node;
        fillInformations(_node);
    }

    private void fillInformations(Figure node)
    {
            textFieldName.setText(node.getName());
            ObservableList<String> obList=list.generateNodeConnections(node);
            listView.setItems(obList);
            textFieldX.setText(((Float)node.getStartPoint().getX()).toString());
            textFieldY.setText(((Float)node.getStartPoint().getY()).toString());
            textFieldRegenNum.setText(String.valueOf(node.getInfo()));
    }

    @FXML
    private void onActionTextField(ActionEvent event)
    {
        Vector2F vec2F= getVector2FFromTextFields();
        Figure node=list.findFigureByName(textFieldName.getText());
        list.changeNodePoint(node,vec2F);
        fillInformations(node);
    }
    private Vector2F getVector2FFromTextFields()
    {
        return new Vector2F(Float.parseFloat(textFieldX.getText()),Float.parseFloat(textFieldY.getText()));
    }

    public TitledPane getTitledPane()
    {
        return titledPane;
    }
}
