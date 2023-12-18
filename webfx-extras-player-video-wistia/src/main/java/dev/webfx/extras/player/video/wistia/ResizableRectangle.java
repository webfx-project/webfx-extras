package dev.webfx.extras.player.video.wistia;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * @author Bruno Salmon
 */
class ResizableRectangle extends Rectangle {

    public ResizableRectangle() {
    }

    public ResizableRectangle(double width, double height) {
        super(width, height);
    }

    public ResizableRectangle(double width, double height, Paint fill) {
        super(width, height, fill);
    }

    public ResizableRectangle(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public void resize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    @Override
    public double minWidth(double height) {
        return 0;
    }

    @Override
    public double minHeight(double width) {
        return 0;
    }

    @Override
    public double maxWidth(double height) {
        return Double.MAX_VALUE;
    }

    @Override
    public double maxHeight(double width) {
        return Double.MAX_VALUE;
    }
}
