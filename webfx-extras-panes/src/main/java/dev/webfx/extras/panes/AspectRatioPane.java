package dev.webfx.extras.panes;

import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public final class AspectRatioPane extends MonoClipPane {

    private final DoubleProperty aspectRatioProperty = FXProperties.newDoubleProperty(1, this::requestLayout);
    private final ObjectProperty<ScaleMode> fitModeProperty = FXProperties.newObjectProperty(ScaleMode.FIT_WIDTH, this::requestLayout);

    public AspectRatioPane() {
        this(null);
    }

    public AspectRatioPane(Node content) {
        this(1, content);
    }

    public AspectRatioPane(double ratio) {
        this(ratio, null);
    }

    public AspectRatioPane(double ratio, Node content) {
        super(content);
        setAspectRatio(ratio);
    }

    public DoubleProperty aspectRatioProperty() {
        return aspectRatioProperty;
    }

    public double getAspectRatio() {
        return aspectRatioProperty.get();
    }

    public void setAspectRatio(double ratio) {
        aspectRatioProperty.set(ratio);
    }

    public ObjectProperty<ScaleMode> fitWidthProperty() {
        return fitModeProperty;
    }

    public ScaleMode getFitMode() {
        return fitModeProperty.get();
    }

    public void setFitMode(ScaleMode fitMode) {
        fitModeProperty.set(fitMode);
    }

    @Override
    public Orientation getContentBias() {
        return getFitMode() == ScaleMode.FIT_HEIGHT ? Orientation.VERTICAL : Orientation.HORIZONTAL;
    }

    @Override
    protected void layoutChildren(double width, double height) {
        double w = width, h = height, arw = h * getAspectRatio(), arh = w / getAspectRatio();
        switch (getFitMode()) {
            case FIT_WIDTH: h = arh; break;
            case FIT_HEIGHT: w = arw; break;
            case BEST_FIT:
                if (arw > w)
                    h = arh;
                else
                    w = arw;
                break;
            case BEST_ZOOM:
                if (arw < w)
                    h = arh;
                else
                    w = arw;
                break;
        }
        layoutInArea(content, width / 2 - w / 2, height / 2 - h / 2, w, h, HPos.CENTER, VPos.CENTER);
        resizeClipIfEnabled();
    }

    @Override
    protected double computeMinWidth(double height) {
        if (height >= 0)
            return height * getAspectRatio();
        return super.computeMinWidth(height);
    }

    @Override
    protected double computePrefWidth(double height) {
        if (height >= 0)
            return height * getAspectRatio();
        return super.computePrefWidth(height);
    }

    @Override
    protected double computeMaxWidth(double height) {
        if (height >= 0)
            return height * getAspectRatio();
        return super.computeMaxWidth(height);
    }

    @Override
    protected double computeMinHeight(double width) {
        if (width >= 0)
            return width / getAspectRatio();
        return super.computeMinHeight(width);
    }

    @Override
    protected double computePrefHeight(double width) {
        if (width >= 0)
            return width / getAspectRatio();
        return super.computePrefHeight(width);
    }

    @Override
    protected double computeMaxHeight(double width) {
        if (width >= 0)
            return width / getAspectRatio();
        return super.computeMaxHeight(width);
    }
}
