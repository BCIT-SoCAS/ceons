package ca.bcit.jfx.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

public class CeonsLabel extends Label {
    private StringProperty tooltipText;

    public CeonsLabel() { super(); }

    public CeonsLabel(String text) { super(text); }

    public CeonsLabel(String text, String tooltipText) {
        super(text);
        this.setTooltipText(tooltipText);
    }

    public final StringProperty tooltipTextProperty() {
        if (tooltipText == null)
            tooltipText = new SimpleStringProperty(this, "tooltipText", "");

        return tooltipText;
    }

    public final void setTooltipText(String value) {
        tooltipTextProperty().setValue(value);
        this.setTooltip(new Tooltip(value));
    }

    public final String getTooltipText() { return tooltipText == null ? "" : tooltipText.getValue(); }
}
