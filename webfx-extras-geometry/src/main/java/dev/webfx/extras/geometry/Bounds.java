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

    default double getCenterX() {
        return (getMaxX() + getMinX()) * 0.5;
    }

    default double getCenterY() {
        return (getMaxY() + getMinY()) * 0.5;
    }

    default boolean contains(double x, double y) {
        return x >= getMinX() && x <= getMaxX() && y >= getMinY() && y <= getMaxY();
    }

    javafx.geometry.Bounds toFXBounds();
}
