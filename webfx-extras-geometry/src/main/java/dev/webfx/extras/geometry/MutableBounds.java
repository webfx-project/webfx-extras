package dev.webfx.extras.geometry;

import javafx.geometry.BoundingBox;

/**
 * @author Bruno Salmon
 */
public class MutableBounds implements Bounds {

    private double x, y, width, height;
    private javafx.geometry.Bounds fxBounds;

    public MutableBounds() {
    }

    public MutableBounds(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        if (x != this.x) {
            this.x = x;
            fxBounds = null;
        }
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        if (y != this.y) {
            this.y = y;
            fxBounds = null;
        }
    }

    @Override
    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        if (width != this.width) {
            this.width = width;
            fxBounds = null;
        }
    }

    @Override
    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        if (height != this.height) {
            this.height = height;
            fxBounds = null;
        }
    }

    public void setMinX(double minX) {
        setX(minX);
    }

    @Override
    public double getMinX() {
        return getX();
    }

    public void setMinY(double minY) {
        setY(minY);
    }

    public double getMinY() {
        return getY();
    }


    @Override
    public javafx.geometry.Bounds toFXBounds() {
        if (fxBounds == null)
            fxBounds = new BoundingBox(x, y, width, height);
        return fxBounds;
    }

    public void copyCoordinates(Bounds other) {
        setMinX(other.getMinX());
        setMinY(other.getMinY());
        setWidth(other.getWidth());
        setHeight(other.getHeight());
    }
}
