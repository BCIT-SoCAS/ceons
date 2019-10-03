package ca.bcit.jfx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class LiveInfoSummaryController implements Initializable {
    @FXML
    private Label info;

    private ResourceBundle resources;

    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
    }

    public void fillInformation(double spectrumBlocked, double regeneratorsBlocked, double linkFailureBlocked, double totalVolume) {
        String blockedSpectrum = spectrumBlocked / totalVolume * 100 + "%";
        String blockedRegenerators = regeneratorsBlocked / totalVolume * 100 + "%";
        String blockedLinkFailure = linkFailureBlocked / totalVolume * 100 + "%";

        info.setText(resources.getString("blocked_spectrum_label") + " " + blockedSpectrum + "\n"
                   + resources.getString("blocked_regenerators_label") + " " + blockedRegenerators + "\n"
                   + resources.getString("blocked_link_failure_label") + " " + blockedLinkFailure);
    }
}
