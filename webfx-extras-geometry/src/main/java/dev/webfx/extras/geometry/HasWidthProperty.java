package dev.webfx.extras.geometry;

import javafx.beans.property.DoubleProperty;

public interface HasWidthProperty {

    DoubleProperty widthProperty();

    default double getWidth() {
        return widthProperty().get();
    }

    default void setWidth(double width) {
        widthProperty().set(width);
    }

}
