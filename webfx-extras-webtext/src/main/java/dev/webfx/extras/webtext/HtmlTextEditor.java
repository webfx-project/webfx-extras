package dev.webfx.extras.webtext;

import dev.webfx.extras.webtext.registry.WebTextRegistry;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Bruno Salmon
 */
public final class HtmlTextEditor extends HtmlText {

    public enum Mode { BASIC, STANDARD, FULL }

    private final ObjectProperty<Mode> modeProperty = new SimpleObjectProperty<>(Mode.FULL);

    public Mode getMode() {
        return modeProperty.get();
    }

    public ObjectProperty<Mode> modeProperty() {
        return modeProperty;
    }

    public void setMode(Mode mode) {
        this.modeProperty.set(mode);
    }

    static {
        WebTextRegistry.registerHtmlTextEditor();
    }
}
