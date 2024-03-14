package dev.webfx.extras.panes;

import javafx.scene.Node;
import javafx.scene.shape.Circle;

/**
 * @author Bruno Salmon
 */
public class CirclePane extends MonoPane {

    private final Circle clip = new Circle(); { setClip(clip); }

    public CirclePane() {
    }

    public CirclePane(Node content) {
        super(content);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        ClipPane.resizeClip(this, clip);
    }
}
