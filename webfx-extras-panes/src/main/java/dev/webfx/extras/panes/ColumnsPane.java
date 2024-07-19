package dev.webfx.extras.panes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class ColumnsPane extends Pane {

    private double fixedColumnWidth = -1;

    private final ObjectProperty<Pos> alignmentProperty = new SimpleObjectProperty<>(Pos.CENTER) {
        protected void invalidated() {
            requestLayout();
        }
    };

    private final DoubleProperty hgapProperty = new SimpleDoubleProperty(0) {
        protected void invalidated() {
            requestLayout();
        }
    };

    public ColumnsPane() {
    }

    public ColumnsPane(Node... children) {
        super(children);
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }

    public double getFixedColumnWidth() {
        return fixedColumnWidth;
    }

    public void setFixedColumnWidth(double fixedColumnWidth) {
        this.fixedColumnWidth = fixedColumnWidth;
    }

    public Pos getAlignment() {
        return alignmentProperty.get();
    }

    public ObjectProperty<Pos> alignmentProperty() {
        return alignmentProperty;
    }

    public void setAlignment(Pos alignment) {
        this.alignmentProperty.set(alignment);
    }

    public double getHgap() {
        return hgapProperty.get();
    }

    public DoubleProperty hgapProperty() {
        return hgapProperty;
    }

    public void setHgap(double hgap) {
        this.hgapProperty.set(hgap);
    }

    @Override
    protected void layoutChildren() {
        List<Node> children = getManagedChildren();
        if (children.isEmpty())
            return;
        Insets insets = getInsets();
        double hgap = getHgap();
        double width = getWidth() - insetsWidth() - hgap * (children.size() - 1), height = getHeight() - insetsHeight();
        double x = insets.getLeft(), y = insets.getTop(), colWidth = getColWidth(width, children.size());
        HPos hpos = getAlignment().getHpos();
        VPos vpos = getAlignment().getVpos();
        for (Node child : children) {
            layoutInArea(child, x, y, colWidth, height, 0, hpos, vpos);
            x += colWidth + hgap;
        }
    }

    private double getColWidth(double totalWidth, int childrenSize) {
        if (fixedColumnWidth > 0)
            return fixedColumnWidth;
        return totalWidth / childrenSize;
    }

    private double insetsWidth() {
        Insets insets = getInsets();
        return insets.getLeft() + insets.getRight();
    }

    private double insetsHeight() {
        Insets insets = getInsets();
        return insets.getTop() + insets.getBottom();
    }

    @Override
    protected double computeMinWidth(double height) {
        double minWidth = 0;
        for (Node child : getManagedChildren())
            minWidth += child.minWidth(height);
        return minWidth + insetsWidth();
    }

    @Override
    protected double computeMinHeight(double width) {
        double minHeight = 0;
        List<Node> children = getManagedChildren();
        if (!children.isEmpty()) {
            double w = width < 0 ? -1 : width / children.size();
            for (Node child : children)
                minHeight = Math.max(minHeight, child.minHeight(w));
        }
        return minHeight + insetsHeight();
    }

    @Override
    protected double computePrefWidth(double height) {
        double prefWidth = 0;
        for (Node child : getManagedChildren())
            prefWidth += child.prefWidth(height);
        return prefWidth + insetsWidth();
    }

    @Override
    protected double computePrefHeight(double width) {
        double prefHeight = 0;
        List<Node> children = getManagedChildren();
        if (!children.isEmpty()) {
            double w = width < 0 ? -1 : width / children.size();
            for (Node child : children)
                prefHeight = Math.max(prefHeight, child.prefHeight(w));
        }
        return prefHeight + insetsHeight();
    }

    @Override
    protected double computeMaxWidth(double height) {
        double maxWidth = 0;
        for (Node child : getManagedChildren())
            maxWidth += child.maxWidth(height);
        return maxWidth + insetsWidth();
    }

    @Override
    protected double computeMaxHeight(double width) {
        double maxHeight = 0;
        List<Node> children = getManagedChildren();
        if (!children.isEmpty()) {
            double w = width < 0 ? -1 : width / children.size();
            for (Node child : children)
                maxHeight = Math.max(maxHeight, child.maxHeight(w));
        }
        return maxHeight + insetsHeight();
    }
}
