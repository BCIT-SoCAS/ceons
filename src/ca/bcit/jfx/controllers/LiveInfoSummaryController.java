package ca.bcit.jfx.controllers;

import ca.bcit.drawing.Figure;
import ca.bcit.drawing.FigureControl;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LiveInfoSummaryController {
    @FXML
    private Label info;

    public void fillInformation(double spectrumBlocked, double regeneratorsBlocked, double linkFailureBlocked, double totalVolume) {
        info.setText("Blocked Spectrum: " + spectrumBlocked / totalVolume * 100 + "%" + "\n"
                + "Blocked Regenerators: " + regeneratorsBlocked / totalVolume * 100 + "%" + "\n"
                + "Blocked Link Failure: " + linkFailureBlocked / totalVolume * 100 + "%");
    }


}
