package dev.webfx.extras.panes;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
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
    private boolean scaleRegion = false;
    private double maxScale = Double.NaN;
    private boolean stretchWidth = false;
    private boolean stretchHeight = false;
    private boolean fillWidth = true;
    private boolean fillHeight = true;
    private HPos hAlignment = HPos.CENTER;
    private VPos vAlignment = VPos.CENTER;
    private double fixedWidth = -1, fixedHeight = -1;

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

    public void setScaleRegion(boolean scaleRegion) {
        this.scaleRegion = scaleRegion;
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

    public void setStretchWidth(boolean stretchWidth) {
        this.stretchWidth = stretchWidth;
    }

    public void setStretchHeight(boolean stretchHeight) {
        this.stretchHeight = stretchHeight;
    }

    public void setFillWidth(boolean fillWidth) {
        this.fillWidth = fillWidth;
    }

    public void setFillHeight(boolean fillHeight) {
        this.fillHeight = fillHeight;
    }

    public void setHAlignment(HPos hAlignment) {
        this.hAlignment = hAlignment;
    }

    public void setVAlignment(VPos vAlignment) {
        this.vAlignment = vAlignment;
    }

    public void setFixedWidth(double fixedWidth) {
        this.fixedWidth = fixedWidth;
        requestLayout();
    }

    public void setFixedHeight(double fixedHeight) {
        this.fixedHeight = fixedHeight;
        requestLayout();
    }

    public void setFixedSize(double forcedWidth, double forcedHeight) {
        setFixedWidth(forcedWidth);
        setFixedHeight(forcedHeight);
    }

    private void computedScales(double width, double height) {
        scale = 1;
        if (scaleEnabled) {
            boolean tryRescale = !content.isResizable() || scaleRegion;
            if (!tryRescale && content instanceof Region) {
                Region region = (Region) content;
                tryRescale = region.minWidth(height) > width || region.maxWidth(height) < width || region.minHeight(width) > height || region.maxHeight(width) < height;
            }
            if (tryRescale) {
                if (width > 0 && height > 0 && content instanceof HasPrefRatio) {
                    double ratio = width / height;
                    System.out.println("ScalePane width = " + width + ", height = " + height + ", ratio = " + ratio);
                    ((HasPrefRatio) content).setPrefRatio(ratio);
                }
                double w = content.prefWidth(height);
                double h = content.prefHeight(width);
                switch (scaleMode) {
                    case FIT_HEIGHT: scale = height == -1 ? 1 : height / h; break;
                    case FIT_WIDTH:  scale = width  == -1 ? 1 : width  / w; break;
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
        double width = fixedWidth != -1 ? fixedWidth : getWidth();
        double height = fixedHeight != -1 ? fixedHeight : getHeight();
        computedScales(width, height);
        content.setScaleX(scaleX);
        content.setScaleY(scaleY);
        boolean stretch = stretchWidth || stretchHeight;
        // With OpenJFX, we can use w = width / scaleX & h = height / scaleY even when stretch = false, but not with
        // WebFX due to scale origin mapping issue (ex: "Java full-stack" card of the WebFX website).
        double w = stretchWidth ?  width / scaleX  : width;
        double h = stretchHeight ? height / scaleY : height;
        double prefWidth = -1, prefHeight = -1;
        Region region = null;
        if (stretch && content instanceof Region) {
            region = (Region) content;
            if (stretchWidth) {
                prefWidth = region.getPrefWidth();
                region.setPrefWidth(w);
            }
            if (stretchHeight) {
                prefHeight = region.getPrefHeight();
                region.setPrefHeight(h);
            }
        }
        double areaY;
        if (vAlignment == VPos.TOP) {
            double unscaledHeight = content.prefHeight(width);
            double scaledHeight = unscaledHeight * scaleY;
            if (scaledHeight < height) {
                areaY = (scaledHeight - unscaledHeight) / 2;
            } else {
                h = height / scaleY;
                areaY = (height - h) / 2;
            }
        } else {
            areaY = (height - h) / 2;
        }
        layoutInArea(content, (width - w) / 2, areaY, w, h, 0, Insets.EMPTY, fillWidth, fillHeight, hAlignment, vAlignment);
        if (stretchWidth && region != null) {
            region.setPrefWidth(prefWidth);
        }
        if (stretchHeight && region != null) {
            region.setPrefHeight(prefHeight);
        }
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
