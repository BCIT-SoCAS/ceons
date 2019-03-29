package ca.bcit.jfx.controllers;

import ca.bcit.drawing.Figure;
import ca.bcit.drawing.Node;
import ca.bcit.drawing.FigureControl;
import ca.bcit.utils.geom.Vector2F;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Admin on 2015-02-02.
 */
public class NodePropertiesController implements Initializable {
    @FXML
		private Label nodeName;
		@FXML
    private Label nodeGroup;
    @FXML
    private Label regenNum;
    @FXML
    private ListView<String> listView;
    @FXML
    private Label xID;
    @FXML
    private Label yID;
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
        nodeName.textProperty().addListener((observable, oldValue, newValue) -> {
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
            fillInformation(node);
            listView.getSelectionModel().clearSelection();
        }
    }
    public void initData(FigureControl _list, Figure _node) {
        list = _list;
        actualNode = _node;
        fillInformation(_node);
    }

    private void fillInformation(Figure fig)
    {
			Node node = (Node) fig;
			nodeName.setText(node.getName());

			Boolean isReplica = (Boolean) node.getNodeGroups().get("replicas");
			Boolean isInternational = (Boolean) node.getNodeGroups().get("international");
			if (Boolean.TRUE.equals(isReplica) && Boolean.TRUE.equals(isInternational)) {
				nodeGroup.setText("Data Center, International");
			} else if (Boolean.TRUE.equals(isReplica)) {
				nodeGroup.setText("Data Center");
			} else if (Boolean.TRUE.equals(isInternational)) {
				nodeGroup.setText("International");
			} else {
				nodeGroup.setText("None");
			}

			ObservableList<String> obList=list.generateNodeConnections(node);
			listView.setItems(obList);
			xID.setText(((Float)node.getStartPoint().getX()).toString());
			yID.setText(((Float)node.getStartPoint().getY()).toString());
			regenNum.setText(Integer.toString(node.getInfo()));
    }

    @FXML
    private void onActionTextField(ActionEvent event)
    {
        Vector2F vec2F= getVector2FFromTextFields();
        Figure node=list.findFigureByName(nodeName.getText());
        list.changeNodePoint(node,vec2F);
        fillInformation(node);
    }

    private Vector2F getVector2FFromTextFields()
    {
        return new Vector2F(Float.parseFloat(xID.getText()),Float.parseFloat(yID.getText()));
    }

    public TitledPane getTitledPane()
    {
        return titledPane;
    }
}
