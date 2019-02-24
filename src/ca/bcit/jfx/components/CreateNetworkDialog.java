package ca.bcit.jfx.components;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import ca.bcit.jfx.controllers.MainWindowController;

public class CreateNetworkDialog {

	public CreateNetworkDialog() {

	}

	public static void display() {
		Stage dialogWindow = new Stage();
		dialogWindow.initModality(Modality.APPLICATION_MODAL);
		dialogWindow.setTitle("Choose Topology Option");

		Button confirmAPIKey = new Button("Confirm");
		Button cancelButton = new Button("Cancel");

		confirmAPIKey.setPrefWidth(220);
		cancelButton.setPrefWidth(220);

		confirmAPIKey.setOnAction(e -> dialogWindow.close());
		cancelButton.setOnAction(e -> dialogWindow.close());
			
		HBox layout = new HBox(10);
		layout.getChildren().addAll(confirmAPIKey, cancelButton);
		layout.setAlignment(Pos.CENTER);
		layout.setSpacing(20);
		Scene scene = new Scene(layout, 520, 300);
		dialogWindow.setScene(scene);
		dialogWindow.showAndWait();
	}
	
}


