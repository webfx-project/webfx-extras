package dev.webfx.extras.panes;

import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public class RatioPane extends MonoPane {

    private double ratio = 1;

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

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }

    @Override
    protected double computeMinHeight(double width) {
        return width / ratio;
    }

    @Override
    protected double computePrefHeight(double width) {
        return width / ratio;
    }

    @Override
    protected double computeMaxHeight(double width) {
        return width / ratio;
    }
}
