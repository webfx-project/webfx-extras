package dev.webfx.extras.webtext;

import dev.webfx.extras.webtext.registry.WebTextRegistry;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Bruno Salmon
 */
public final class HtmlTextEditor extends HtmlText {

    public enum Mode { BASIC, STANDARD, FULL }

    {
        // Tell JavaFX that it can accept focus
        setFocusTraversable(true);
        // Note: this is the responsibility of the peer to actually set the JavaFX scene focus owner to this node when
        // it actually gains the focus.
    }

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
