package dev.webfx.extras.panes;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * @author Bruno Salmon
 */
public class MonoClipPane extends MonoPane {

    private final Shape clip;
    private final BooleanProperty clipEnabledProperty = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            setClip(isClipEnabled() ? clip : null);
        }
    };

    public MonoClipPane() {
        this(null);
    }

    public MonoClipPane(Node content) {
        this(false, content);
    }

    public MonoClipPane(boolean circle) {
        this(circle, null);
    }

    public MonoClipPane(boolean circle, Node content) {
        super(content);
        clip = circle ? new Circle() : new Rectangle();
        setClipEnabled(true); // enabled by default
    }

    public BooleanProperty clipEnabledProperty() {
        return clipEnabledProperty;
    }

    public boolean isClipEnabled() {
        return clipEnabledProperty.get();
    }

    public void setClipEnabled(boolean clipEnabled) {
        clipEnabledProperty.set(clipEnabled);
    }

    protected void resizeClip() {
        double width = getWidth(), height = getHeight();
        if (clip instanceof Rectangle) {
            Rectangle r = (Rectangle) clip;
            r.setWidth(width);
            r.setHeight(height);
        } else if (clip instanceof Circle) {
            Circle c = (Circle) clip;
            c.setCenterX(width / 2);
            c.setCenterY(height / 2);
            c.setRadius(Math.min(width, height) / 2);
        }
    }

    protected void resizeClipIfEnabled() {
        if (isClipEnabled())
            resizeClip();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        resizeClipIfEnabled();
    }

}
