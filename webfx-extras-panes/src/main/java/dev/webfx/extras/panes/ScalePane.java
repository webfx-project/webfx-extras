package dev.webfx.extras.panes;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 * @author Bruno Salmon
 */
public class ScalePane extends MonoPane {

    private ScaleMode scaleMode;
    private boolean scaleEnabled = true;
    private boolean canGrow = true, canShrink = true;
    private boolean canScaleX = true, canScaleY = true;
    private boolean alwaysTry = false;
    private double maxScale = Double.NaN;
    private double scale;

    private double scaleX, scaleY;

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
        setContent(node);
    }

    public Node getNode() {
        return getContent();
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

    private void computedScales(double width, double height) {
        scale = 1;
        if (scaleEnabled) {
            boolean tryRescale = !content.isResizable() || alwaysTry;
            if (!tryRescale && content instanceof Region) {
                Region region = (Region) content;
                tryRescale = region.minWidth(height) > width || region.maxWidth(height) < width || region.minHeight(width) > height || region.maxHeight(width) < height;
            }
            if (tryRescale) {
                double w = content.prefWidth(height), h = content.prefHeight(width);
                switch (scaleMode) {
                    case FIT_HEIGHT: scale = height == -1 ? 1 : height / h; break;
                    case FIT_WIDTH:  scale = width == -1 ? 1 : width  / w; break;
                    case BEST_FIT:   scale = Math.min(height == -1 ? 1 : height / h, width == -1 ? 1 : width / w); break;
                    case BEST_ZOOM:  scale = Math.max(height == -1 ? 1 : height / h, width == -1 ? 1 : width / w); break;
                }
                if (!canShrink && scale < 1 || !canGrow && scale > 1)
                    scale = 1;
            }
            if (scale > maxScale)
                scale = maxScale;
        }
        scaleX = canScaleX ? scale : 1;
        scaleY = canScaleY ? scale : 1;
    }

    @Override
    protected void layoutChildren() {
        if (content == null)
            return;
        double width = getWidth();
        double height = getHeight();
        computedScales(width, height);
        content.setScaleX(scaleX);
        content.setScaleY(scaleY);
        layoutInArea(content, 0, 0, width, height, 0, HPos.CENTER, VPos.CENTER);
    }

    @Override
    protected double computeContentMinWidth(double height) {
        return 0;
    }

    @Override
    protected double computeContentMinHeight(double width) {
        return 0;
    }

    @Override
    protected double computeContentPrefWidth(double height) {
        double prefWidth = super.computeContentPrefWidth(height);
        computedScales(prefWidth, height);
        return scaleX * prefWidth;
    }

    @Override
    protected double computeContentPrefHeight(double width) {
        double prefHeight = super.computeContentPrefHeight(width);
        computedScales(width, prefHeight);
        return scaleY * prefHeight;
    }

    @Override
    protected double computeMaxWidth(double height) {
        return Double.MAX_VALUE;
    }

    @Override
    protected double computeMaxHeight(double width) {
        return Double.MAX_VALUE;
    }

}
