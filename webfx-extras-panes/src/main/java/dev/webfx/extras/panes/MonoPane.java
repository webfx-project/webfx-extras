package dev.webfx.extras.panes;

import dev.webfx.platform.util.collection.Collections;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
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
    protected boolean internalSync;

    private final ObjectProperty<Node> contentProperty = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            onContentChanged(get());
        }
    };

    private final ObjectProperty<Pos> alignmentProperty = new SimpleObjectProperty<>(Pos.CENTER) {
        protected void invalidated() {
            requestLayout();
        }
    };

    {
        // Automatically setting the content field from the unique child when children is changed by the application code
        getChildren().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (internalSync)
                    return;
                if (getChildren().size() > 1)
                    throw new IllegalStateException();
                internalSync = true;
                setContent(Collections.first(getChildren()));
                internalSync = false;
            }
        });
    }

    public MonoPane() {
    }

    public MonoPane(Node content) {
        setContent(content);
    }

    public Node getContent() {
        return contentProperty.get();
    }

    public ObjectProperty<Node> contentProperty() {
        return contentProperty;
    }

    public void setContent(Node content) {
        if (content != this.content)
            contentProperty.set(content);
    }

    protected void onContentChanged(Node newContent) {
        if (!internalSync) {
            internalSync = true;
            if (newContent == null)
                getChildren().clear();
            else if (newContent != content) // Skipping if same newContent - important for WebView in browser (resetting iFrame will unload it)
                getChildren().setAll(newContent);
            internalSync = false;
        }
        content = newContent;
    }

    public Pos getAlignment() {
        return alignmentProperty.get();
    }

    public ObjectProperty<Pos> alignmentProperty() {
        return alignmentProperty;
    }

    public void setAlignment(Pos alignment) {
        this.alignmentProperty.set(alignment);
    }

    @Override
    protected void layoutChildren() {
        if (content != null) {
            double width = getWidth(), height = getHeight();
            Insets insets = getInsets();
            layoutInArea(content, insets.getLeft(), insets.getTop()
                    , width - insetsWidth(), height - insetsHeight()
                    , 0, getAlignment().getHpos(), getAlignment().getVpos());
        }
    }


    @Override
    public Orientation getContentBias() {
        return content == null ? super.getContentBias() : content.getContentBias();
    }

    protected double insetsWidth() {
        Insets insets = getInsets();
        return insets.getLeft() + insets.getRight();
    }

    protected double insetsHeight() {
        Insets insets = getInsets();
        return insets.getTop() + insets.getBottom();
    }

    @Override
    protected double computeMinWidth(double height) {
        return insetsWidth() + computeContentMinWidth(height);
    }

    protected double computeContentMinWidth(double height) {
        return content == null ? 0 : content.minWidth(height);
    }

    @Override
    protected double computeMinHeight(double width) {
        return insetsHeight() + computeContentMinHeight(width);
    }

    protected double computeContentMinHeight(double width) {
        return content == null ? 0 : content.minHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        return insetsWidth() + computeContentPrefWidth(height);
    }

    protected double computeContentPrefWidth(double height) {
        return content == null ? 0 : content.prefWidth(height);
    }

    @Override
    protected double computePrefHeight(double width) {
        return insetsHeight() + computeContentPrefHeight(width);
    }

    protected double computeContentPrefHeight(double width) {
        return content == null ? 0 : content.prefHeight(width);
    }

    @Override
    protected double computeMaxWidth(double height) {
        return insetsWidth() + computeContentMaxWidth(height);
    }

    protected double computeContentMaxWidth(double height) {
        return content == null ? 0 : content.maxWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width) {
        return insetsHeight() + computeContentMaxHeight(width);
    }

    protected double computeContentMaxHeight(double width) {
        return content == null ? 0 : content.maxHeight(width);
    }

}
