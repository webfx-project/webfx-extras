package dev.webfx.extras.webview.pane;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * @author Bruno Salmon
 */
public class ResizableRectangle extends Rectangle {

    private double minWith = 0;
    private double maxWith = Double.MAX_VALUE;
    private double minHeight = 0;
    private double maxHeight = Double.MAX_VALUE;

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

    public void setMinSize(double minWidth, double minHeight) {
        this.minWith = minWidth;
        this.minHeight = minHeight;
    }

    public void setMaxSize(double maxWidth, double maxHeight) {
        this.maxWith = maxWidth;
        this.maxHeight = maxHeight;
    }

    @Override
    public double minWidth(double height) {
        return minWith;
    }

    @Override
    public double minHeight(double width) {
        return minHeight;
    }

    @Override
    public double maxWidth(double height) {
        return maxWith;
    }

    @Override
    public double maxHeight(double width) {
        return maxHeight;
    }
}
