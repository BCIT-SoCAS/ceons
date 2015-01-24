package mtk.eon.jfx;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import mtk.eon.ApplicationResources;
import mtk.eon.jfx.tasks.ProjectLoadingTask;
import mtk.eon.net.MetricType;
import mtk.eon.net.Modulation;
import mtk.eon.net.Network;

public class FXMLController {
	
	@FXML private VBox settings;
	@FXML private ToggleGroup regeneratorsMetric;
	@FXML private ToggleGroup modulationMetric;
	
	@FXML public void loadNetworkAction(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File file = fileChooser.showOpenDialog(settings.getScene().getWindow());
		
		ProjectLoadingTask task = new ProjectLoadingTask(file);
		Thread thread = new Thread(task);
		thread.start();
	}

	@FXML public void startSimulationAction(ActionEvent e) {
		Network network = ApplicationResources.getProject().getNetwork();
		
		for (Modulation modulation : network.getAllowedModulations()) network.disallowModulation(modulation);
		for (Modulation modulation : Modulation.values())
			if (((CheckBox) settings.lookup("#modulation" + modulation.ordinal())).isSelected()) 
				network.allowModulation(modulation);
		
		for (Toggle toggle : modulationMetric.getToggles()) if (toggle.isSelected())
			network.setModualtionMetricType(MetricType.valueOf2(((RadioButton) toggle).getText()));
		settings.disableProperty().set(true);
	}
	
	@FXML public void textFieldUIntFilter(KeyEvent e) {
		TextField source = (TextField) e.getSource();
		char typed = e.getCharacter().charAt(0);
		if (source.getText().startsWith("0")) source.setText(String.valueOf(Integer.parseInt(source.getText())));
		if (typed < '0' || typed > '9' || source.getText().length() == 0 && typed =='0') e.consume();
	}
}
