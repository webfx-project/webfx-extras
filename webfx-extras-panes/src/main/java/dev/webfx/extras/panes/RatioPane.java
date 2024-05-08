package dev.webfx.extras.panes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public final class RatioPane extends MonoPane {

    private final DoubleProperty ratioProperty = new SimpleDoubleProperty(1) {
        @Override
        protected void invalidated() {
            requestLayout();
        }
    };

    public RatioPane() {
        this(null);
    }

    public RatioPane(Node content) {
        this(1, content);
    }

    public RatioPane(double ratio) {
        this(ratio, null);
    }

    public RatioPane(double ratio, Node content) {
        super(content);
        setRatio(ratio);
    }

    public DoubleProperty ratioProperty() {
        return ratioProperty;
    }

    public double getRatio() {
        return ratioProperty.get();
    }

    public void setRatio(double ratio) {
        ratioProperty.set(ratio);
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }

    @Override
    protected double computeMinHeight(double width) {
        return width / getRatio();
    }

    @Override
    protected double computePrefHeight(double width) {
        return width / getRatio();
    }

    @Override
    protected double computeMaxHeight(double width) {
        return width / getRatio();
    }
}
