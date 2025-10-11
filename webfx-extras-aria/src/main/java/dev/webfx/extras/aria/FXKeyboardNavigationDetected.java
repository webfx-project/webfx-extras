package dev.webfx.extras.aria;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * @author Bruno Salmon
 */
public final class FXKeyboardNavigationDetected {

    private static final BooleanProperty keyboardNavigationDetectedProperty = new SimpleBooleanProperty();

    public static BooleanProperty keyboardNavigationDetectedProperty() {
        return keyboardNavigationDetectedProperty;
    }

    public static void setKeyboardNavigationDetected(boolean keyboardNavigationDetected) {
        keyboardNavigationDetectedProperty.set(keyboardNavigationDetected);
    }

    public static boolean isKeyboardNavigationDetected() {
        return keyboardNavigationDetectedProperty.get();
    }

}
