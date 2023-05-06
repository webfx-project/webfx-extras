package dev.webfx.extras.geometry;

import javafx.beans.property.DoubleProperty;

public interface HasHeightProperty {

    DoubleProperty heightProperty();

    default double getHeight() {
        return heightProperty().get();
    }

    default void setHeight(double height) {
        heightProperty().set(height);
    }

}
