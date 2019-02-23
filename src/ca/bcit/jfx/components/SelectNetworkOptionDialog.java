package ca.bcit.jfx.components;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import ca.bcit.jfx.controllers.MainWindowController;

public class SelectNetworkOptionDialog {

	public SelectNetworkOptionDialog() {
	
	}

	public static void display() {
		Stage dialogWindow = new Stage();
		dialogWindow.initModality(Modality.APPLICATION_MODAL);
		dialogWindow.setTitle("Choose Topology Option");

		Button loadNetworkBtn = new Button("Load Network Topology");
		Button createNewBtn = new Button("Create Network Topology");

		loadNetworkBtn.setPrefWidth(220);
		createNewBtn.setPrefWidth(220);

		loadNetworkBtn.setOnAction(e -> dialogWindow.close());
		createNewBtn.setOnAction(e -> dialogWindow.close());
			
		HBox layout = new HBox(10);
		layout.getChildren().addAll(loadNetworkBtn, createNewBtn);
		layout.setAlignment(Pos.CENTER);
		layout.setSpacing(20);
		Scene scene = new Scene(layout, 520, 300);
		dialogWindow.setScene(scene);
		dialogWindow.showAndWait();
	}
	
}


