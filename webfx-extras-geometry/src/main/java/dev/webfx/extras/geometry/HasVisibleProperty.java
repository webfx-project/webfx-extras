package dev.webfx.extras.geometry;

import javafx.beans.property.BooleanProperty;

public interface HasVisibleProperty {

    BooleanProperty visibleProperty();

    default boolean isVisible() {
        return visibleProperty().get();
    }

    default void setVisible(boolean visible) {
        visibleProperty().set(visible);
    }

}
