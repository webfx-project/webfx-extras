package dev.webfx.extras.panes;

import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.*;
import javafx.scene.Node;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Bruno Salmon
 */
public final class ColumnsPane extends LayoutPane {

    private final ObjectProperty<Pos> alignmentProperty = newLayoutObjectProperty(Pos.CENTER);

    private final DoubleProperty hgapProperty = newLayoutDoubleProperty();

    private final DoubleProperty vgapProperty = newLayoutDoubleProperty();

    private final DoubleProperty fixedColumnWidthProperty = newLayoutDoubleProperty();

    private final DoubleProperty minColumnWidthProperty = newLayoutDoubleProperty();

    private final IntegerProperty fixedColumnCountProperty = newLayoutIntegerProperty();

    private final IntegerProperty maxColumnCountProperty = newLayoutIntegerProperty();

    private final DoubleProperty minRowHeightProperty = newLayoutDoubleProperty();

    private <T> ObjectProperty<T> newLayoutObjectProperty(T initialValue) {
        return FXProperties.newObjectProperty(initialValue, this::requestLayout);
    }

    private DoubleProperty newLayoutDoubleProperty() {
        return FXProperties.newDoubleProperty(this::requestLayout);
    }

    private IntegerProperty newLayoutIntegerProperty() {
        return FXProperties.newIntegerProperty(this::requestLayout);
    }

    public ColumnsPane() {
    }

    public ColumnsPane(double hgap) {
        setHgap(hgap);
    }

    public ColumnsPane(double hgap, double vgap) {
        this(hgap);
        setVgap(vgap);
    }

    public ColumnsPane(Node... children) {
        super(children);
    }

    public ColumnsPane(double hgap, Node... children) {
        super(children);
        setHgap(hgap);
    }

    public ColumnsPane(double hgap, double vgap, Node... children) {
        super(children);
        setHgap(hgap);
        setVgap(vgap);
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
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
        hgapProperty.set(hgap);
    }

    public double getVgap() {
        return vgapProperty.get();
    }

    public DoubleProperty vgapProperty() {
        return vgapProperty;
    }

    public void setVgap(double hgap) {
        vgapProperty.set(hgap);
    }

    public double getFixedColumnWidth() {
        return fixedColumnWidthProperty.get();
    }

    public DoubleProperty fixedColumnWidthProperty() {
        return fixedColumnWidthProperty;
    }

    public void setFixedColumnWidth(double fixedColumnWidth) {
        fixedColumnWidthProperty.set(fixedColumnWidth);
    }

    public double getMinColumnWidth() {
        return minColumnWidthProperty.get();
    }

    public DoubleProperty minColumnWidthProperty() {
        return minColumnWidthProperty;
    }

    public void setMinColumnWidth(double minColumnWidth) {
        minColumnWidthProperty.set(minColumnWidth);
    }

    public int getFixedColumnCount() {
        return fixedColumnCountProperty.get();
    }

    public IntegerProperty fixedColumnCountProperty() {
        return fixedColumnCountProperty;
    }

    public void setFixedColumnCount(int fixedColumnCount) {
        fixedColumnCountProperty.set(fixedColumnCount);
    }

    public IntegerProperty maxColumnCountProperty() {
        return maxColumnCountProperty;
    }

    public void setMaxColumnCount(int fixedColumnCount) {
        maxColumnCountProperty.set(fixedColumnCount);
    }

    public int getMaxColumnCount() {
        return maxColumnCountProperty.get();
    }

    public DoubleProperty minRowHeightProperty() {
        return minRowHeightProperty;
    }

    public void setMinRowHeight(double minRowHeight) {
        minRowHeightProperty.set(minRowHeight);
    }

    @Override
    protected void layoutChildren() {
        double width = getWidth(), height = getHeight();
        int colCount = computeColumnCount(width);
        if (colCount == 0)
            return;
        double hgap = getHgap(), vgap = getVgap();
        double widthNoGap = width - insetsWidth() - hgap * (colCount - 1);
        double w = getColWidth(widthNoGap, colCount), h = height - insetsHeight();
        List<Node> children = getManagedChildren();
        boolean multiRows = colCount < children.size();
        if (multiRows) {
            h = -vgap; // this is to keep y unchanged on first row (see y += h + vgap below)
        }
        Insets insets = getInsets();
        double x = insets.getLeft(), y = insets.getTop();
        HPos hpos = getAlignment().getHpos();
        VPos vpos = getAlignment().getVpos();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            if (multiRows && i % colCount == 0) { // Detecting new row (including the first one)
                x = insets.getLeft();
                y += h + vgap;
                h = computeRowHeight(children, 0, colCount, w, Node::prefHeight);
            }
            layoutInArea(child, x, y, w, h, 0, hpos, vpos);
            x += w + hgap;
        }
    }

    public double getColWidth() {
        double width = getWidth();
        int colCount = computeColumnCount(width);
        if (colCount == 0)
            return 0;
        double hgap = getHgap();
        double widthNoGap = width - insetsWidth() - hgap * (colCount - 1);
        return getColWidth(widthNoGap, colCount);
    }

    private double getColWidth(double totalWidthNoGap, int colCount) {
        double fixedColumnWidth = getFixedColumnWidth();
        if (fixedColumnWidth > 0)
            return fixedColumnWidth;
        return totalWidthNoGap / colCount;
    }

    private int computeColumnCount(double width) {
        List<Node> children = getManagedChildren();
        if (children.isEmpty())
            return 0;
        int n = children.size();
        int fixedColumnCount = getFixedColumnCount();
        if (fixedColumnCount > 0)
            return Math.min(n, fixedColumnCount);
        double fixedColumnWidth = getFixedColumnWidth();
        if (fixedColumnWidth > 0) {
            int nmax = Math.max(1, (int) ((width - insetsWidth() + getHgap()) / (fixedColumnWidth + getHgap())));
            return Math.min(n, nmax);
        }
        double minColumnWidth = getMinColumnWidth();
        while (minColumnWidth > 0 && n > 1) {
            double widthNoGap = width - insetsWidth() - getHgap() * (n - 1);
            double w = getColWidth(widthNoGap, n);
            if (w >= minColumnWidth)
                break;
            n--;
        }
        int maxColumnProperty = maxColumnCountProperty.get();
        if (maxColumnProperty > 0 && n > maxColumnProperty)
            n = maxColumnProperty;
        return n;
    }

    private boolean isMultipleRowsEnabled() {
        return getFixedColumnCount() > 0 ||  getMaxColumnCount() > 0 || getMinColumnWidth() > 0 || getFixedColumnWidth() > 0;
    }

    private double computeRowHeight(List<Node> children, int rowFirstChildIndex, int colCount, double colWidth, BiFunction<Node, Double, Double> cellHeightFunction) {
        double h = 0;
        for (int i = rowFirstChildIndex, n = Math.min(rowFirstChildIndex + colCount, children.size()); i < n; i++) {
            Node node = children.get(i);
            double nodeHeight = boundedNodeHeightWithBias(node, colWidth, cellHeightFunction.apply(node, colWidth), true, true);
            h = Math.max(h, nodeHeight);
        }
        double minRowHeight = minRowHeightProperty.get();
        if (minRowHeight > 0 && h < minRowHeight)
            h = minRowHeight;
        return h;
    }

    @Override
    protected double computeMinWidth(double height) {
        double fixedColumnWidth = getFixedColumnWidth();
        if (fixedColumnWidth > 0)
            return fixedColumnWidth + insetsWidth();
        return computeMinPrefMaxWidth(height, Node::minWidth);
    }

    @Override
    protected double computePrefWidth(double height) {
        return computeMinPrefMaxWidth(height, Node::prefWidth);
    }

    @Override
    protected double computeMaxWidth(double height) {
        return computeMinPrefMaxWidth(height, Node::maxWidth);
    }

    private double computeMinPrefMaxWidth(double height, BiFunction<Node, Double, Double> minPrefMaxWidthFunction) {
        boolean multipleRowsEnabled = isMultipleRowsEnabled();
        int fixedColumnCount = getFixedColumnCount(), colIndex = 0;
        double fixedColumnWidth = getFixedColumnWidth();
        double minPrefMaxWidth = 0, rowMinPrefMaxWidth = 0;
        for (Node child : getManagedChildren()) {
            if (rowMinPrefMaxWidth > 0) {
                rowMinPrefMaxWidth += getHgap();
            }
            if (fixedColumnWidth > 0)
                rowMinPrefMaxWidth += fixedColumnWidth;
            else {
                rowMinPrefMaxWidth += minPrefMaxWidthFunction.apply(child, height);
                if (multipleRowsEnabled) {
                    if (fixedColumnCount <= 0 /* min = 1 column */ || ++colIndex >= fixedColumnCount) {
                        minPrefMaxWidth = Math.max(rowMinPrefMaxWidth, minPrefMaxWidth);
                        rowMinPrefMaxWidth = 0;
                    }
                }
            }
        }
        minPrefMaxWidth = Math.max(rowMinPrefMaxWidth, minPrefMaxWidth);
        // Checking that we get at least minColumnWidth (if set)
        double minColumnWidth = getMinColumnWidth();
        if (minColumnWidth > 0 && minPrefMaxWidth < minColumnWidth)
            minPrefMaxWidth = minColumnWidth;
        // Adding insets width
        return minPrefMaxWidth + insetsWidth();
    }

    @Override
    protected double computeMinHeight(double width) {
        return computeHeight(width, Node::prefHeight);
    }

    protected double computePrefHeight(double width) {
        return computeHeight(width, Node::prefHeight);
    }

    protected double computeMaxHeight(double width) {
        return computeHeight(width, Node::prefHeight);
    }

    private double computeHeight(double width, BiFunction<Node, Double, Double> heightFunction) {
        double totalHeight = 0;
        List<Node> children = getManagedChildren();
        if (!children.isEmpty()) {
            int colCount = computeColumnCount(width);
            double widthNoGap = width - insetsWidth() - getHgap() * (colCount - 1);
            double colWidth = width < 0 ? -1 : getColWidth(widthNoGap, colCount);
            for (int childIndex = 0; childIndex < children.size(); childIndex += colCount) {
                double rowHeight = computeRowHeight(children, childIndex, colCount, colWidth, heightFunction);
                if (childIndex > 0)
                    rowHeight += getVgap();
                totalHeight += rowHeight;
            }
        }
        return totalHeight + insetsHeight();
    }
}
