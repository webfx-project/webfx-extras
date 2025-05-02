package dev.webfx.extras.panes;

import javafx.geometry.Pos;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public final class GrowingPane extends MonoPane {

    private double minContentMinWidth, minContentPrefWidth, minContentMaxWidth;
    private double minContentMinHeight, minContentPrefHeight, minContentMaxHeight;

    public GrowingPane() {
        this(null);
    }

    public GrowingPane(Node content) {
        super(content);
        // Setting the default alignment to TOP_CENTER (so the growing space will be at the bottom)
        setAlignment(Pos.TOP_CENTER);
    }

    public void reset() {
        minContentMinWidth = minContentPrefWidth = minContentMaxWidth =
                minContentMinHeight = minContentPrefHeight = minContentMaxHeight = -1;
    }

    @Override
    protected double computeContentMinWidth(double height) {
        double contentMinWidth = super.computeContentMinWidth(height);
        if (contentMinWidth > minContentMinWidth)
            minContentMinWidth = contentMinWidth;
        else
            contentMinWidth = minContentMinWidth;
        return contentMinWidth;
    }

    @Override
    protected double computeContentMinHeight(double width) {
        double contentMinHeight = super.computeContentMinHeight(width);
        if (contentMinHeight > minContentMinHeight)
            minContentMinHeight = contentMinHeight;
        else
            contentMinHeight = minContentMinHeight;
        return contentMinHeight;
    }

    @Override
    protected double computeContentPrefWidth(double height) {
        double contentPrefWidth = super.computeContentPrefWidth(height);
        if (contentPrefWidth > minContentPrefWidth)
            minContentPrefWidth = contentPrefWidth;
        else
            contentPrefWidth = minContentPrefWidth;
        return contentPrefWidth;
    }

    @Override
    protected double computeContentPrefHeight(double width) {
        double contentPrefHeight = super.computeContentPrefHeight(width);
        if (contentPrefHeight > minContentPrefHeight)
            minContentPrefHeight = contentPrefHeight;
        else
            contentPrefHeight = minContentPrefHeight;
        return contentPrefHeight;
    }

    @Override
    protected double computeContentMaxWidth(double height) {
        double contentMaxWidth = super.computeContentMaxWidth(height);
        if (contentMaxWidth > minContentMaxWidth)
            minContentMaxWidth = contentMaxWidth;
        else
            minContentMaxWidth = contentMaxWidth;
        return contentMaxWidth;
    }

    @Override
    protected double computeContentMaxHeight(double width) {
        double contentMaxHeight = super.computeContentMaxHeight(width);
        if (contentMaxHeight > minContentMaxHeight)
            minContentMaxHeight = contentMaxHeight;
        else
            contentMaxHeight = minContentMaxHeight;
        return contentMaxHeight;
    }
}
