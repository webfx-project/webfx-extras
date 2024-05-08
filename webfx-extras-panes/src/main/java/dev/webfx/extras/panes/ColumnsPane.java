package dev.webfx.extras.panes;

import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class ColumnsPane extends Pane {

    private double fixedColumnWidth = -1;

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

    @Override
    protected void layoutChildren() {
        List<Node> children = getManagedChildren();
        if (children.isEmpty())
            return;
        double width = getWidth(), height = getHeight();
        double x = 0, y = 0, colWidth = getColWidth(width, children.size());
        for (Node child : children) {
            layoutInArea(child, x, y, colWidth, height, 0, HPos.CENTER, VPos.CENTER);
            x += colWidth;
        }
    }

    private double getColWidth(double totalWidth, int childrenSize) {
        if (fixedColumnWidth > 0)
            return fixedColumnWidth;
        return totalWidth / childrenSize;
    }

    @Override
    protected double computeMinWidth(double height) {
        double minWidth = 0;
        for (Node child : getManagedChildren())
            minWidth += child.minWidth(height);
        return minWidth;
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
        return minHeight;
    }

    @Override
    protected double computePrefWidth(double height) {
        double prefWidth = 0;
        for (Node child : getManagedChildren())
            prefWidth += child.prefWidth(height);
        return prefWidth;
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
        return prefHeight;
    }

    @Override
    protected double computeMaxWidth(double height) {
        double maxWidth = 0;
        for (Node child : getManagedChildren())
            maxWidth += child.maxWidth(height);
        return maxWidth;
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
        return maxHeight;
    }
}
