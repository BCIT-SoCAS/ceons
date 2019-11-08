package ca.bcit.jfx.controllers;

import ca.bcit.utils.LocaleUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class LiveInfoSummaryController implements Initializable {
    @FXML
    private Label info;

    public void initialize(URL location, ResourceBundle resources) {

    }

    public void fillInformation(double spectrumBlocked, double regeneratorsBlocked, double linkFailureBlocked, double totalVolume) {
        String blockedSpectrum = spectrumBlocked / totalVolume * 100 + "%";
        String blockedRegenerators = regeneratorsBlocked / totalVolume * 100 + "%";
        String blockedLinkFailure = linkFailureBlocked / totalVolume * 100 + "%";

        info.setText(LocaleUtils.translate("blocked_spectrum_label") + " " + blockedSpectrum + "\n"
                   + LocaleUtils.translate("blocked_regenerators_label") + " " + blockedRegenerators + "\n"
                   + LocaleUtils.translate("blocked_link_failure_label") + " " + blockedLinkFailure);
    }
}
