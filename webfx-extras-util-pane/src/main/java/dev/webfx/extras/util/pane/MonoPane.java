package dev.webfx.extras.util.pane;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * MonoPane is a JavaFX Pane designed to work with a single child called content. It acts as a wrapper of that content
 * and computes its min/pref/max width/height to match the content.
 *
 * @author Bruno Salmon
 */
public class MonoPane extends Pane {

    private Node content;

    public MonoPane() {
    }

    public MonoPane(Node content) {
        setContent(content);
    }

    public Node getContent() {
        return content;
    }

    public void setContent(Node content) {
        this.content = content;
        if (content == null)
            getChildren().clear();
        else
            getChildren().setAll(content);
    }

    @Override
    protected void layoutChildren() {
        if (content != null) {
            double width = getWidth(), height = getHeight();
            Insets padding = getPadding();
            layoutInArea(content, padding.getLeft(), padding.getTop()
                    , width - paddingWidth(), height - paddingHeight()
                    , 0, HPos.CENTER, VPos.BOTTOM);
            // Note: the VPos.BOTTOM is for a possible folding animation from bottom to top (used for header tabs
            // in Modality backoffice). Should this be parameterised?
        }
    }


    @Override
    public Orientation getContentBias() {
        return content == null ? super.getContentBias() : content.getContentBias();
    }

    protected double paddingWidth() {
        Insets padding = getPadding();
        return padding.getLeft() + padding.getRight();
    }

    protected double paddingHeight() {
        Insets padding = getPadding();
        return padding.getTop() + padding.getBottom();
    }

    @Override
    protected double computeMinWidth(double height) {
        return paddingWidth() + (content == null ?  0 : content.minWidth(height));
    }

    @Override
    protected double computeMinHeight(double width) {
        return paddingHeight() + (content == null ?  0 : content.minHeight(width));
    }

    @Override
    protected double computePrefWidth(double height) {
        return paddingWidth() + (content == null ?  0 : content.prefWidth(height));
    }

    @Override
    protected double computePrefHeight(double width) {
        return paddingHeight() + (content == null ?  0 : content.prefHeight(width));
    }

    @Override
    protected double computeMaxWidth(double height) {
        return paddingWidth() + (content == null ?  0 : content.maxWidth(height));
    }

    @Override
    protected double computeMaxHeight(double width) {
        return paddingHeight() + (content == null ?  0 : content.maxHeight(width));
    }

}
