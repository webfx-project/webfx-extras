package dev.webfx.extras.panes;

import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.util.collection.Collections;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;

/**
 * MonoPane is a JavaFX Pane designed to work with a single child called content. It acts as a wrapper of that content
 * and computes its min/pref/max width/height to match the content.
 *
 * @author Bruno Salmon
 */
public class MonoPane extends LayoutPane {

    protected Node content;
    protected boolean internalSync;

    private final ObjectProperty<Node> contentProperty = FXProperties.newObjectProperty(this::onContentChanged);

    private final ObjectProperty<Pos> alignmentProperty = FXProperties.newObjectProperty(Pos.CENTER, this::requestLayout);

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
        if (content != getContent() && !contentProperty.isBound())
            contentProperty.set(content);
    }

    protected void onContentChanged(Node newContent) {
        Node oldContent = content;
        content = newContent;
        if (!internalSync) {
            internalSync = true;
            if (newContent == null)
                getChildren().clear();
            else if (newContent != oldContent) // Skipping if same newContent - important for WebView in browser (resetting iFrame will unload it)
                getChildren().setAll(newContent);
            internalSync = false;
        }
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
    protected void layoutChildren(double paddingLeft, double paddingTop, double innerWidth, double innerHeight) {
        if (content != null) {
            layoutInArea(content, paddingLeft, paddingTop, innerWidth, innerHeight, getAlignment());
        }
    }

    @Override
    public Orientation getContentBias() {
        return content == null ? null : content.getContentBias();
    }

    @Override
    protected double computeMinWidth(double height) { // pane height
        return insetsWidth() + computeContentMinWidth(height < 0 ? -1 : height - insetsHeight());
    }

    protected double computeContentMinWidth(double height) { // content height
        return content == null ? 0 : content.minWidth(height);
    }

    @Override
    protected double computeMinHeight(double width) { // pane width
        return insetsHeight() + computeContentMinHeight(width < 0 ? -1 : width - insetsWidth());
    }

    protected double computeContentMinHeight(double width) { // content width
        return content == null ? 0 : content.minHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) { // pane height
        return insetsWidth() + computeContentPrefWidth(height < 0 ? -1 : height - insetsHeight());
    }

    protected double computeContentPrefWidth(double height) { // content height
        return content == null ? 0 : content.prefWidth(height);
    }

    @Override
    protected double computePrefHeight(double width) { // pane width
        return insetsHeight() + computeContentPrefHeight(width < 0 ? -1 : width - insetsWidth());
    }

    protected double computeContentPrefHeight(double width) { // content width
        return content == null ? 0 : content.prefHeight(width);
    }

    @Override
    protected double computeMaxWidth(double height) { // pane height
        return insetsWidth() + computeContentMaxWidth(height < 0 ? -1 : height - insetsHeight());
    }

    protected double computeContentMaxWidth(double height) { // content height
        return content == null ? 0 : content.maxWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width) { // pane width
        return insetsHeight() + computeContentMaxHeight(width < 0 ? -1 : width - insetsWidth());
    }

    protected double computeContentMaxHeight(double width) { // content width
        return content == null ? 0 : content.maxHeight(width);
    }

}
