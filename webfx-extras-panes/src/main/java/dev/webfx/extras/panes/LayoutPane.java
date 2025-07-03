package dev.webfx.extras.panes;

import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * @author Bruno Salmon
 */
public abstract class LayoutPane extends Pane {

    public LayoutPane() {
        super();
    }

    public LayoutPane(Node... children) {
        super(children);
    }

    @Override
    protected void layoutChildren() {
        layoutChildren(getLayoutWidth(), getLayoutHeight());
    }

    protected double getLayoutWidth() {
        return getWidth();
    }

    protected double getLayoutHeight() {
        return getHeight();
    }

    protected double insetsWidth() {
        Insets insets = getInsets();
        return insets.getLeft() + insets.getRight();
    }

    protected double insetsHeight() {
        Insets insets = getInsets();
        return insets.getTop() + insets.getBottom();
    }

    protected void layoutChildren(double width, double height) {
        Insets insets = getInsets();
        double innerWidth = width - insetsWidth();
        double innerHeight = height - insetsHeight();
        layoutChildren(insets.getLeft(), insets.getTop(), innerWidth, innerHeight);
    }

    protected void layoutChildren(double paddingLeft, double paddingTop, double innerWidth, double innerHeight) {
        super.layoutChildren();
    }

    protected void layoutInArea(Node child, double areaX, double areaY,
                                double areaWidth, double areaHeight) {
        layoutInArea(child, areaX, areaY, areaWidth, areaHeight, Pos.TOP_LEFT); // most efficient if POS is not necessary
    }
    protected void layoutInArea(Node child, double areaX, double areaY,
                                double areaWidth, double areaHeight,
                                Pos pos) {
        layoutInArea(child, areaX, areaY, areaWidth, areaHeight, pos.getHpos(), pos.getVpos());
    }

    protected void layoutInArea(Node child, double areaX, double areaY,
                                double areaWidth, double areaHeight,
                                HPos halignment, VPos valignment) {
        layoutInArea(child, areaX, areaY, areaWidth, areaHeight, 0, null ,halignment, valignment);
    }

    // Same implementation as Region.boundedNodeSizeWithBias() which unfortunately is not public

    public static double boundedNodeWidthWithBias(Node node, double areaWidth, double areaHeight,
                                         boolean fillWidth, boolean fillHeight) {
        Orientation bias = node.getContentBias();

        double childWidth;

        if (bias == null) {
            childWidth = boundedSize(
                node.minWidth(-1), fillWidth ? areaWidth
                    : Math.min(areaWidth, node.prefWidth(-1)),
                node.maxWidth(-1));
        } else if (bias == Orientation.HORIZONTAL) {
            childWidth = boundedSize(
                node.minWidth(-1), fillWidth ? areaWidth
                    : Math.min(areaWidth, node.prefWidth(-1)),
                node.maxWidth(-1));
        } else { // bias == VERTICAL
            double childHeight = boundedSize(
                node.minHeight(-1), fillHeight ? areaHeight
                    : Math.min(areaHeight, node.prefHeight(-1)),
                node.maxHeight(-1));
            childWidth = boundedSize(
                node.minWidth(childHeight), fillWidth ? areaWidth
                    : Math.min(areaWidth, node.prefWidth(childHeight)),
                node.maxWidth(childHeight));
        }

        return childWidth;
    }

    public static double boundedNodeHeightWithBias(Node node, double areaWidth, double areaHeight,
                                                     boolean fillWidth, boolean fillHeight) {
        Orientation bias = node.getContentBias();

        double childHeight;

        if (bias == null) {
            childHeight = boundedSize(
                node.minHeight(-1), fillHeight ? areaHeight
                    : Math.min(areaHeight, node.prefHeight(-1)),
                node.maxHeight(-1));
        } else if (bias == Orientation.HORIZONTAL) {
            double childWidth = boundedSize(
                node.minWidth(-1), fillWidth ? areaWidth
                    : Math.min(areaWidth, node.prefWidth(-1)),
                node.maxWidth(-1));
            childHeight = boundedSize(
                node.minHeight(childWidth), fillHeight ? areaHeight
                    : Math.min(areaHeight, node.prefHeight(childWidth)),
                node.maxHeight(childWidth));

        } else { // bias == VERTICAL
            childHeight = boundedSize(
                node.minHeight(-1), fillHeight ? areaHeight
                    : Math.min(areaHeight, node.prefHeight(-1)),
                node.maxHeight(-1));
        }

        return childHeight;
    }

    public static double boundedSize(double min, double pref, double max) {
        double a = Math.max(pref, min);
        double b = Math.max(min, max);
        return Math.min(a, b);
    }
}
