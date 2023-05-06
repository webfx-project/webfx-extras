package dev.webfx.extras.bounds;

/**
 * @author Bruno Salmon
 */
public class FXBoundsWrapper implements Bounds {

    private javafx.geometry.Bounds fxBounds;

    public FXBoundsWrapper() {
    }

    public FXBoundsWrapper(javafx.geometry.Bounds fxBounds) {
        this.fxBounds = fxBounds;
    }

    public void setFxBounds(javafx.geometry.Bounds fxBounds) {
        this.fxBounds = fxBounds;
    }

    @Override
    public double getMinX() {
        return fxBounds.getMinX();
    }

    @Override
    public double getMinY() {
        return fxBounds.getMinY();
    }

    @Override
    public double getWidth() {
        return fxBounds.getWidth();
    }

    @Override
    public double getHeight() {
        return fxBounds.getHeight();
    }

    @Override
    public javafx.geometry.Bounds toFXBounds() {
        return fxBounds;
    }

}
