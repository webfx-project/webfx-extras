package dev.webfx.extras.scalepane;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * @author Bruno Salmon
 */
public class ScalePane extends Pane {

    private ScaleMode scaleMode;
    private Node node;
    private boolean scaleEnabled = true;
    private boolean canGrow = true, canShrink = true;
    private boolean canScaleX = true, canScaleY = true;
    private boolean alwaysTry = false;
    private double maxScale = Double.NaN;
    private double scale;

    public ScalePane() {
        this((Node) null);
    }

    public ScalePane(ScaleMode scaleMode) {
        this(scaleMode, null);
    }

    public ScalePane(Node node) {
        this(ScaleMode.BEST_FIT, node);
    }

    public ScalePane(ScaleMode scaleMode, Node node) {
        this.scaleMode = scaleMode;
        setNode(node);
    }

    public void setNode(Node node) {
        this.node = node;
        if (node != null)
            getChildren().setAll(node);
        else
            getChildren().clear();
    }

    public Node getNode() {
        return node;
    }

    public ScaleMode getScaleMode() {
        return scaleMode;
    }

    public void setScaleMode(ScaleMode scaleMode) {
        this.scaleMode = scaleMode;
    }

    public boolean isScaleEnabled() {
        return scaleEnabled;
    }

    public void setScaleEnabled(boolean scaleEnabled) {
        this.scaleEnabled = scaleEnabled;
        requestLayout();
    }

    public void setCanGrow(boolean canGrow) {
        this.canGrow = canGrow;
        requestLayout();
    }

    public void setCanShrink(boolean canShrink) {
        this.canShrink = canShrink;
        requestLayout();
    }

    public void setCanScaleX(boolean canScaleX) {
        this.canScaleX = canScaleX;
        requestLayout();
    }

    public void setCanScaleY(boolean canScaleY) {
        this.canScaleY = canScaleY;
        requestLayout();
    }

    public void setAlwaysTry(boolean alwaysTry) {
        this.alwaysTry = alwaysTry;
        requestLayout();
    }

    public double getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(double maxScale) {
        this.maxScale = maxScale;
        requestLayout();
    }

    public double getScale() {
        return scale;
    }

    @Override
    protected void layoutChildren() {
        if (node == null)
            return;
        double width = getWidth();
        double height = getHeight();
        scale = 1;
        if (scaleEnabled) {
            boolean tryRescale = !node.isResizable() || alwaysTry;
            if (!tryRescale && node instanceof Region) {
                Region region = (Region) this.node;
                tryRescale = region.minWidth(height) > width || region.maxWidth(height) < width || region.minHeight(width) > height || region.maxHeight(width) < height;
            }
            if (tryRescale) {
                double w = node.prefWidth(height), h = node.prefHeight(width);
                switch (scaleMode) {
                    case FIT_HEIGHT: scale = height / h; break;
                    case FIT_WIDTH:  scale = width  / w; break;
                    case BEST_FIT:   scale = Math.min(height / h, width / w); break;
                    case BEST_ZOOM:  scale = Math.max(height / h, width / w); break;
                }
                if (!canShrink && scale < 1 || !canGrow && scale > 1)
                    scale = 1;
            }
            if (scale > maxScale)
                scale = maxScale;
        }
        node.setScaleX(canScaleX ? scale : 1);
        node.setScaleY(canScaleY ? scale : 1);
        layoutInArea(node, 0, 0, width, height, 0, HPos.CENTER, VPos.CENTER);
    }
}
