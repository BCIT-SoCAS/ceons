package ca.bcit;

import ca.bcit.io.YamlSerializable;
import ca.bcit.io.project.EONProjectFileFormat;
import ca.bcit.io.project.ProjectFileFormat;
import ca.bcit.net.Network;
import ca.bcit.net.NetworkLink;
import ca.bcit.net.NetworkNode;
import ca.bcit.net.demand.generator.AnycastDemandGenerator;
import ca.bcit.net.demand.generator.TrafficGenerator;
import ca.bcit.net.demand.generator.UnicastDemandGenerator;
import ca.bcit.utils.random.ConstantRandomVariable;
import ca.bcit.utils.random.IrwinHallRandomVariable;
import ca.bcit.utils.random.MappedRandomVariable;
import ca.bcit.utils.random.UniformRandomVariable;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("/ca/bcit/jfx/res/MainWindow.fxml"));
		GridPane root = (GridPane)loader.load();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setMinWidth(primaryStage.getWidth());
		primaryStage.setMinHeight(primaryStage.getHeight());
		
		final Canvas canvas = (Canvas) scene.lookup("#graph");
		BorderPane pane = (BorderPane) scene.lookup("#borderPane");
		canvas.widthProperty().bind(pane.widthProperty());
		canvas.heightProperty().bind(pane.heightProperty());
		System.out.println(canvas.getBoundsInParent());
//		canvas.getGraphicsContext2D().fillRect(10, 10, 20, 20);
//		canvas.setOnMouseDragged(e -> { canvas.getGraphicsContext2D().fillRect(e.getX(), e.getY(), 10, 10); });
	}
	
	public static void main(String[] args) {
		try {
			YamlSerializable.registerSerializableClass(NetworkNode.class);
			YamlSerializable.registerSerializableClass(NetworkLink.class);
			YamlSerializable.registerSerializableClass(Network.class);
			
			YamlSerializable.registerSerializableClass(MappedRandomVariable.class);
			YamlSerializable.registerSerializableClass(UniformRandomVariable.Generic.class);
			YamlSerializable.registerSerializableClass(ConstantRandomVariable.class);
			YamlSerializable.registerSerializableClass(IrwinHallRandomVariable.Integer.class);
			YamlSerializable.registerSerializableClass(UnicastDemandGenerator.class);
			YamlSerializable.registerSerializableClass(AnycastDemandGenerator.class);
			YamlSerializable.registerSerializableClass(TrafficGenerator.class);
			
			ProjectFileFormat.registerFileFormat(new EONProjectFileFormat());
			launch(args);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Fatal error occured: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	

}
