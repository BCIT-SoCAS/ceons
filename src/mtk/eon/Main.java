package mtk.eon;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.swing.JOptionPane;

import mtk.eon.io.YamlSerializable;
import mtk.eon.io.project.EONProjectFileFormat;
import mtk.eon.io.project.LegacyProjectFileFormat;
import mtk.eon.io.project.ProjectFileFormat;
import mtk.eon.net.Network;
import mtk.eon.net.NetworkLink;
import mtk.eon.net.NetworkNode;
import mtk.eon.net.demand.generator.AnycastDemandGenerator;
import mtk.eon.net.demand.generator.TrafficGenerator;
import mtk.eon.net.demand.generator.UnicastDemandGenerator;
import mtk.eon.utils.random.ConstantRandomVariable;
import mtk.eon.utils.random.IrwinHallRandomVariable;
import mtk.eon.utils.random.MappedRandomVariable;
import mtk.eon.utils.random.UniformRandomVariable;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("/mtk/eon/jfx/res/MainWindow.fxml"));
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
			ProjectFileFormat.registerFileFormat(new LegacyProjectFileFormat());
			launch(args);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Fatal error occured: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	// Pytania do michala:
	//   - Czemu inaczej wyglada tabelka do dynamicznej metryki modulacji w pracy i w programie?
	//   - Czemu pod koniec unifikacji modulacji metryka sciezki liczona jest inaczej?
	
	// ZDEFINIOWAC OPCJE CZY ANYCAST PO ROZLACZENIU MUSI DO TEGO SAMEGO DC
}
