package dev.webfx.extras.responsive;

import javafx.beans.value.ObservableValue;

/**
 * @author Bruno Salmon
 */
public interface ResponsiveLayout {

    default ObservableValue<?>[] getResponsiveDependencies() {
        return new ObservableValue[0];
    }

    void applyResponsiveLayout();

}
