package dev.webfx.extras.panes;

import dev.webfx.platform.console.Console;
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
    private boolean log;

    double unscaledContentWidth, unscaledContentHeight;

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

    public void setLog(boolean log) {
        this.log = log;
    }

    @Override
    public Orientation getContentBias() {
        // Necessary to return Orientation.VERTICAL in FIT_HEIGHT mode to have the correct prefWidth computation (ex: Modality web menu)
        // Necessary to return Orientation.HORIZONTAL in FIT_WIDTH mode to have the correct prefHeight computation
        return scaleMode == ScaleMode.FIT_HEIGHT ? Orientation.VERTICAL : Orientation.HORIZONTAL;
        // TODO: investigate if we should return different content bias in some other cases
        // Note: putting a child with HORIZONTAL content bias in a GridPane (ex: Modality Kitchen activity) creates an infinite layout loop in OpenJFX 18, 19 & 20, but this is fixed in OpenJFX 21.
    }

    private void computedScales(double scalePaneInnerWidth, double scalePaneInnerHeight) {
        scale = 1;
        if (scaleEnabled && content != null) {
            boolean tryRescale = !content.isResizable() || scaleRegion;
            if (!tryRescale && content instanceof Region) {
                Region region = (Region) content;
                tryRescale = region.minWidth(scalePaneInnerHeight) > scalePaneInnerWidth || region.maxWidth(scalePaneInnerHeight) < scalePaneInnerWidth || region.minHeight(scalePaneInnerWidth) > scalePaneInnerHeight || region.maxHeight(scalePaneInnerWidth) < scalePaneInnerHeight;
            }
            if (tryRescale) {
                if (scalePaneInnerWidth > 0 && scalePaneInnerHeight > 0 && content instanceof Scalable) {
                    ((Scalable) content).prepareScale(0,
                            (contentWidth, contentHeight) -> ScalePane.this.computeScale(scalePaneInnerWidth, scalePaneInnerHeight, contentWidth, contentHeight));
                }
                unscaledContentWidth = boundedNodeWidthWithBias(content, scalePaneInnerWidth, scalePaneInnerHeight, fillWidth, fillHeight);
                unscaledContentHeight = boundedSize(content.minHeight(unscaledContentWidth), content.prefHeight(unscaledContentWidth), content.maxHeight(unscaledContentWidth));
                scale = computeScale(scalePaneInnerWidth, scalePaneInnerHeight, unscaledContentWidth, unscaledContentHeight);
                if (!canShrink && scale < 1 || !canGrow && scale > 1)
                    scale = 1;
            }
            if (scale > maxScale)
                scale = maxScale;
        }
        scaleX = canScaleX ? scale : 1;
        scaleY = canScaleY ? scale : 1;
        if (log)
            Console.log("scalePaneInnerWidth = " + scalePaneInnerWidth + ", scalePaneInnerHeight = " + scalePaneInnerHeight + ", scale = " + scale);
    }

    private double computeScale(double scalePaneWidth, double scalePaneHeight, double unscaledContentWidth, double unscaledContentHeight) {
        switch (scaleMode) {
            case FIT_HEIGHT: return scalePaneHeight < 0 ? 1 : scalePaneHeight / unscaledContentHeight;
            case FIT_WIDTH:  return scalePaneWidth  < 0 ? 1 : scalePaneWidth  / unscaledContentWidth;
            case BEST_FIT:   return Math.min(scalePaneHeight < 0 ? 1 : scalePaneHeight / unscaledContentHeight, scalePaneWidth < 0 ? 1 : scalePaneWidth / unscaledContentWidth);
            case BEST_ZOOM:  return Math.max(scalePaneHeight < 0 ? 1 : scalePaneHeight / unscaledContentHeight, scalePaneWidth < 0 ? 1 : scalePaneWidth / unscaledContentWidth);
        }
        return 1;
    }

    @Override
    protected void layoutChildren(double paddingLeft, double paddingTop, double innerWidth, double innerHeight) {
        if (content == null)
            return;
        /*if (log)
            Console.log("ScalePane.layoutChildren(" + innerWidth + ", " + innerHeight + ")");*/
        innerWidth = fixedWidth != -1 ? fixedWidth - insetsWidth() : innerWidth;
        innerHeight = fixedHeight != -1 ? fixedHeight - insetsHeight() : innerHeight;
        computedScales(innerWidth, innerHeight);
        content.setScaleX(scaleX);
        content.setScaleY(scaleY);
        boolean stretch = stretchWidth || stretchHeight;
        // With OpenJFX, we can use w = width / scaleX & h = height / scaleY even when stretch = false, but not with
        // WebFX due to the scale origin mapping issue (ex: "Java full-stack" card of the WebFX website).
        double w = stretchWidth ?  innerWidth / scaleX  : unscaledContentWidth;
        double h = stretchHeight ? innerHeight / scaleY : unscaledContentHeight;
        double memorisedPrefWidth = -1, memorisedPrefHeight = -1;
        Region region = null;
        if (stretch && content instanceof Region) {
            region = (Region) content;
            if (stretchWidth) {
                memorisedPrefWidth = region.getPrefWidth();
                region.setPrefWidth(w);
            }
            if (stretchHeight) {
                memorisedPrefHeight = region.getPrefHeight();
                region.setPrefHeight(h);
            }
        }
        double areaY;
        if (vAlignment == VPos.TOP) {
            double unscaledHeight = content.prefHeight(innerWidth);
            double scaledHeight = unscaledHeight * scaleY;
            if (scaledHeight < innerHeight) {
                areaY = (scaledHeight - unscaledHeight) / 2;
            } else {
                h = innerHeight / scaleY;
                areaY = (innerHeight - h) / 2;
            }
        } else {
            areaY = (innerHeight - h) / 2;
        }
        layoutInArea(content, paddingLeft + (innerWidth - w) / 2, paddingTop + areaY, w, h, 0, Insets.EMPTY, fillWidth, fillHeight, hAlignment, vAlignment);
        if (stretchWidth && region != null) {
            region.setPrefWidth(memorisedPrefWidth);
        }
        if (stretchHeight && region != null) {
            region.setPrefHeight(memorisedPrefHeight);
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
    protected double computeContentPrefWidth(double scalePaneInnerHeight) {
        //double prefWidth = super.computeContentPrefWidth(scalePaneInnerHeight);
        computedScales(-1, scalePaneInnerHeight);
        return scaleX * unscaledContentWidth;
    }

    @Override
    protected double computePrefHeight(double width) {
        if (fixedHeight > 0)
            return fixedHeight;
        return super.computePrefHeight(width);
    }

    @Override
    protected double computeContentPrefHeight(double scalePaneInnerWidth) {
        if (fixedHeight > 0)
            return fixedHeight;
        computedScales(scalePaneInnerWidth, -1);
        return scaleY * unscaledContentHeight;
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
