package dev.webfx.extras.panes;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * @author Bruno Salmon
 */
public class ClipPane extends Pane {

    private final Shape clip;

    public ClipPane(Node... children) {
        this(false, children);
    }

    public ClipPane(boolean circle, Node... children) {
        super(children);
        clip = circle ? new Circle() : new Rectangle();
        setClip(clip);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        resizeClip();
    }

    protected void resizeClip() {
        resizeClip(this, clip);
    }

    public static void resizeClip(Region region, Shape clip) {
        double width = region.getWidth(), height = region.getHeight();
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

}
