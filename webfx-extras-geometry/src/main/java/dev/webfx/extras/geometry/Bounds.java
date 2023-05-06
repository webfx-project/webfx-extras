package dev.webfx.extras.geometry;

public interface Bounds {

    double getMinX();

    double getMinY();

    double getWidth();

    double getHeight();

    default double getMaxX() {
        return getMinX() + getWidth();
    }

    default double getMaxY() {
        return getMinY() + getHeight();
    }

    javafx.geometry.Bounds toFXBounds();
}
