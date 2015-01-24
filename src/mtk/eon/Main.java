package mtk.eon;

import java.io.IOException;
import java.io.PrintStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.swing.JOptionPane;

import mtk.eon.jfx.ConsoleInputStream;
import mtk.eon.jfx.ConsoleOutputStream;

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
//		System.out.println(canvas.getBoundsInParent());
		canvas.getGraphicsContext2D().fillRect(10, 10, 20, 20);
		canvas.setOnMouseDragged(e -> { canvas.getGraphicsContext2D().fillRect(e.getX(), e.getY(), 10, 10); });
		((Button) scene.lookup("#toggleInput")).setOnAction(e -> { scene.lookup("#input").setVisible(!scene.lookup("#input").isVisible()); });
		System.setOut(new PrintStream(new ConsoleOutputStream((TextArea) scene.lookup("#console"))));
		System.setIn(new ConsoleInputStream((TextField) scene.lookup("#input")));
		
//		Scanner s = new Scanner(System.in);
//		for (int i = 0; i < 5; i++) {
//			System.out.print("Podaj " + (i + 1) + ": ");
//			String line = s.nextLine();
//			System.out.println(line);
//		}
//		s.close();
	}
	
	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Fatal error occured: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}
