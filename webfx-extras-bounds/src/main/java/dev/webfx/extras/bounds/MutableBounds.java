package dev.webfx.extras.bounds;

import javafx.geometry.BoundingBox;

/**
 * @author Bruno Salmon
 */
public class MutableBounds implements Bounds {

    private double x, y, width, height;
    private javafx.geometry.Bounds fxBounds;

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

    @Override
    public double getMinX() {
        return getX();
    }

    public double getMinY() {
        return getY();
    }


    @Override
    public javafx.geometry.Bounds toFXBounds() {
        if (fxBounds == null) {
            return new BoundingBox(x, y, width, height);
        }
        return fxBounds;
    }
}
