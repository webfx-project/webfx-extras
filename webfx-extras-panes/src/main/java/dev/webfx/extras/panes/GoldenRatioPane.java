package dev.webfx.extras.panes;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;

/**
 *
 * @author Bruno Salmon
 */
public class GoldenRatioPane extends MonoPane {

    private static final double GOLDEN_RATIO = 1.618;

    {
        setMaxHeight(Double.MAX_VALUE);
    }

    public GoldenRatioPane() {
    }

    public GoldenRatioPane(Node content) {
        super(content);
    }

    @Override
    protected void layoutChildren() {
        Node child = getContent();
        if (child == null)
            return;
        double width = getWidth() - insetsWidth(), height = getHeight() - insetsWidth();
        double w = width, h = child.prefHeight(w);
        double x = getInsets().getLeft() + Math.max(0, width / 2 - w / 2);
        double y = getInsets().getTop();
        double extraHeight = height - h;
        if (extraHeight > 0) {
            y += extraHeight / (1 + GOLDEN_RATIO);
        }
        layoutInArea(child, x, y, w, h, 0, HPos.CENTER, VPos.CENTER);
    }

}
