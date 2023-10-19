package dev.webfx.extras.panes;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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

    protected Node content;
    private HPos contentHalignment = HPos.CENTER;
    private VPos contentValignment = VPos.CENTER;

    {
        getChildren().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (getChildren().isEmpty())
                    content = null;
                else if (getChildren().size() == 1)
                    content = getChildren().get(0);
                else
                    throw new IllegalStateException();
            }
        });
    }

    public MonoPane() {
    }

    public MonoPane(Node content) {
        setContent(content);
    }

    public Node getContent() {
        return content;
    }

    public void setContent(Node content) {
        if (content == null)
            getChildren().clear();
        else
            getChildren().setAll(content);
    }

    public HPos getContentHalignment() {
        return contentHalignment;
    }

    public void setContentHalignment(HPos contentHalignment) {
        this.contentHalignment = contentHalignment;
    }

    public VPos getContentValignment() {
        return contentValignment;
    }

    public void setContentValignment(VPos contentValignment) {
        this.contentValignment = contentValignment;
    }

    @Override
    protected void layoutChildren() {
        if (content != null) {
            double width = getWidth(), height = getHeight();
            Insets padding = getPadding();
            layoutInArea(content, padding.getLeft(), padding.getTop()
                    , width - paddingWidth(), height - paddingHeight()
                    , 0, contentHalignment, contentValignment);
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
        return paddingWidth() + computeContentMinWidth(height);
    }

    protected double computeContentMinWidth(double height) {
        return content == null ?  0 : content.minWidth(height);
    }

    @Override
    protected double computeMinHeight(double width) {
        return paddingHeight() + computeContentMinHeight(width);
    }

    protected double computeContentMinHeight(double width) {
        return content == null ?  0 : content.minHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        return paddingWidth() + computeContentPrefWidth(height);
    }

    protected double computeContentPrefWidth(double height) {
        return content == null ?  0 : content.prefWidth(height);
    }

    @Override
    protected double computePrefHeight(double width) {
        return paddingHeight() + computeContentPrefHeight(width);
    }

    protected double computeContentPrefHeight(double width) {
        return content == null ?  0 : content.prefHeight(width);
    }

    @Override
    protected double computeMaxWidth(double height) {
        return paddingWidth() + computeContentMaxWidth(height);
    }

    protected double computeContentMaxWidth(double height) {
        return content == null ?  0 : content.maxWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width) {
        return paddingHeight() + computeContentMaxHeight(width);
    }

    protected double computeContentMaxHeight(double width) {
        return content == null ?  0 : content.maxHeight(width);
    }

}
