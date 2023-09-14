package dev.webfx.extras.util.pane;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * @author Bruno Salmon
 */
public class ClipPane extends Pane {

    private final Rectangle clip = new Rectangle();
    {
        setClip(clip);
    }

    public ClipPane() {
    }

    public ClipPane(Node... children) {
        super(children);
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
