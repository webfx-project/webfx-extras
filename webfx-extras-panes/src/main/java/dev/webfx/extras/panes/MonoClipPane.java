package dev.webfx.extras.panes;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

/**
 * @author Bruno Salmon
 */
public class MonoClipPane extends MonoPane {

    private final Rectangle clip = new Rectangle();
    private final BooleanProperty clipEnabledProperty = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            setClip(get() ? clip : null);
        }
    };

    public MonoClipPane() {
        this(null);
    }

    public MonoClipPane(Node content) {
        super(content);
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
        clip.setWidth(getWidth());
        clip.setHeight(getHeight());
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if (isClipEnabled())
            resizeClip();
    }

}
