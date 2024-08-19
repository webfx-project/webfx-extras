package dev.webfx.extras.panes;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL; // Necessary to have correct prefHeight computation when scaling images in FIT_WIDTH mode
        // TODO: investigate if we should return different content bias in some other cases
        // Note: putting a child with HORIZONTAL content bias in a GridPane (ex: Modality Kitchen activity) creates an infinite layout loop in OpenJFX 18, 19 & 20, but this is fixed in OpenJFX 21.
    }

    private void computedScales(double width, double height) {
        scale = 1;
        if (scaleEnabled && content != null) {
            boolean tryRescale = !content.isResizable() || scaleRegion;
            if (!tryRescale && content instanceof Region) {
                Region region = (Region) content;
                tryRescale = region.minWidth(height) > width || region.maxWidth(height) < width || region.minHeight(width) > height || region.maxHeight(width) < height;
            }
            if (tryRescale) {
                if (width > 0 && height > 0 && content instanceof Scalable) {
                    ((Scalable) content).prepareScale(0,
                            (contentWidth, contentHeight) -> ScalePane.this.computeScale(width, height, contentWidth, contentHeight));
                }
                double w = Math.max(content.minWidth(height), content.prefWidth(height));
                double h = content.prefHeight(width);
                scale = computeScale(width, height, w, h);
                if (!canShrink && scale < 1 || !canGrow && scale > 1)
                    scale = 1;
            }
            if (scale > maxScale)
                scale = maxScale;
        }
        scaleX = canScaleX ? scale : 1;
        scaleY = canScaleY ? scale : 1;
    }

    private double computeScale(double width, double height, double w, double h) {
        switch (scaleMode) {
            case FIT_HEIGHT: return height < 0 ? 1 : height / h;
            case FIT_WIDTH:  return width  < 0 ? 1 : width  / w;
            case BEST_FIT:   return Math.min(height < 0 ? 1 : height / h, width < 0 ? 1 : width / w);
            case BEST_ZOOM:  return Math.max(height < 0 ? 1 : height / h, width < 0 ? 1 : width / w);
        }
        return 1;
    }

    @Override
    protected void layoutChildren() {
        if (content == null)
            return;
        double width = fixedWidth != -1 ? fixedWidth : getWidth();
        double height = fixedHeight != -1 ? fixedHeight : getHeight();
        Insets insets = getInsets();
        width -= insets.getLeft() + insets.getRight();
        height -= insets.getTop() + insets.getBottom();
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
        layoutInArea(content, insets.getLeft() + (width - w) / 2, insets.getTop() + areaY, w, h, 0, Insets.EMPTY, fillWidth, fillHeight, hAlignment, vAlignment);
        if (stretchWidth && region != null) {
            region.setPrefWidth(prefWidth);
        }
        if (stretchHeight && region != null) {
            region.setPrefHeight(prefHeight);
        }
    }

    @Override
    protected double computeMinWidth(double height) {
        if (fixedWidth > 0)
            return fixedWidth;
        return super.computeMinWidth(height);
    }

    @Override
    protected double computeContentMinWidth(double height) {
        if (canScaleX && canShrink)
            return 0;
        return super.computeContentMinWidth(height);
    }

    @Override
    protected double computeMinHeight(double width) {
        if (fixedHeight > 0)
            return fixedHeight;
        // In FIT_WIDTH mode, we always want the whole height to be displayed (ex: GP class image in Modality front-office)
        if (scaleMode == ScaleMode.FIT_WIDTH)
            return computePrefHeight(width);
        return super.computeMinHeight(width);
    }

    @Override
    protected double computeContentMinHeight(double width) {
        if (canScaleY && canShrink)
            return 0;
        return super.computeContentMinHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        if (fixedWidth > 0)
            return fixedWidth;
        return super.computePrefWidth(height);
    }

    @Override
    protected double computeContentPrefWidth(double height) {
        double prefWidth = super.computeContentPrefWidth(height);
        computedScales(prefWidth, height);
        return scaleX * prefWidth;
    }

    @Override
    protected double computePrefHeight(double width) {
        if (fixedHeight > 0)
            return fixedHeight;
        return super.computePrefHeight(width);
    }

    @Override
    protected double computeContentPrefHeight(double width) {
        if (fixedHeight > 0)
            return fixedHeight;
        double prefHeight = super.computeContentPrefHeight(width);
        computedScales(width, prefHeight);
        return scaleY * prefHeight;
    }

    @Override
    protected double computeMaxWidth(double height) {
        if (fixedWidth > 0)
            return fixedWidth;
        if (canScaleX && canGrow && Double.isNaN(maxScale))
            return Double.MAX_VALUE;
        return super.computeContentMaxWidth(height) * (canScaleX && canGrow ? maxScale : 1);
    }

    @Override
    protected double computeMaxHeight(double width) {
        if (fixedHeight > 0)
            return fixedHeight;
        if (canScaleY && canGrow && Double.isNaN(maxScale))
            return Double.MAX_VALUE;
        return super.computeContentMaxHeight(width) * (canScaleY && canGrow ? maxScale : 1);
    }

}
