package dev.webfx.extras.util.pane;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

/**
 * @author Bruno Salmon
 */
public class MonoClipPane extends MonoPane {

    private final Rectangle clip = new Rectangle();
    {
        setClip(clip);
        setMinHeight(0);
    }

    public MonoClipPane() {
    }

    public MonoClipPane(Node content) {
        super(content);
    }

    protected void resizeClip() {
        clip.setWidth(getWidth());
        clip.setHeight(getHeight());
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        resizeClip();
    }

}
