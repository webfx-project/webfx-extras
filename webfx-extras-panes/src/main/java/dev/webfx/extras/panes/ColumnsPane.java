package dev.webfx.extras.panes;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public final class ColumnsPane extends Pane {

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

    private final DoubleProperty vgapProperty = new SimpleDoubleProperty(0) {
        protected void invalidated() {
            requestLayout();
        }
    };

    private final DoubleProperty fixedColumnWidthProperty = new SimpleDoubleProperty(0) {
        protected void invalidated() {
            requestLayout();
        }
    };

    private final DoubleProperty minColumnWidthProperty = new SimpleDoubleProperty(0) {
        protected void invalidated() {
            requestLayout();
        }
    };

    private final IntegerProperty fixedColumnCountProperty = new SimpleIntegerProperty(0) {
        protected void invalidated() {
            requestLayout();
        }
    };

    private final IntegerProperty maxColumnCountProperty = new SimpleIntegerProperty(0) {
        protected void invalidated() {
            requestLayout();
        }
    };

    private final DoubleProperty minRowHeightProperty = new SimpleDoubleProperty(0) {
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
            h = computeRowMinPrefMaxHeight(children, 0, colCount, node -> node.prefHeight(w));
        }
        Insets insets = getInsets();
        double x = insets.getLeft(), y = insets.getTop();
        HPos hpos = getAlignment().getHpos();
        VPos vpos = getAlignment().getVpos();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            layoutInArea(child, x, y, w, h, 0, hpos, vpos);
            x += w + hgap;
            if (multiRows && x >= width) {
                x = insets.getLeft();
                y += h + vgap;
                h = computeRowMinPrefMaxHeight(children, i, colCount, node -> node.prefHeight(w));
            }
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
        int fixedColumnCount = getFixedColumnCount();
        if (fixedColumnCount > 0)
            return fixedColumnCount;
        List<Node> children = getManagedChildren();
        if (children.isEmpty())
            return 0;
        int n = children.size();
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
        return getFixedColumnCount() > 0 ||  getMaxColumnCount() > 0 ||getMinColumnWidth() > 0;
    }

    private double computeRowMinPrefMaxHeight(List<Node> children, int rowFirstChildIndex, int colCount, Function<Node, Double> minPrefMaxHeightFunction) {
        double h = children.stream().skip(rowFirstChildIndex).limit(colCount).mapToDouble(minPrefMaxHeightFunction::apply).max().orElse(0);
        double minRowHeight = minRowHeightProperty.get();
        if (minRowHeight > 0 && h < minRowHeight)
            h = minRowHeight;
        return h;
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
        double minPrefMaxWidth = 0, rowMinPrefMaxWidth = 0;
        for (Node child : getManagedChildren()) {
            if (rowMinPrefMaxWidth > 0) {
                rowMinPrefMaxWidth += getHgap();
            }
            rowMinPrefMaxWidth += minPrefMaxWidthFunction.apply(child, height);
            if (multipleRowsEnabled) {
                if (fixedColumnCount <= 0 /* min = 1 column */ || ++colIndex >= fixedColumnCount) {
                    minPrefMaxWidth = Math.max(rowMinPrefMaxWidth, minPrefMaxWidth);
                    rowMinPrefMaxWidth = 0;
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
        return computeMinPrefMaxHeight(width, Node::minHeight);
    }

    protected double computePrefHeight(double width) {
        return computeMinPrefMaxHeight(width, Node::prefHeight);
    }

    protected double computeMaxHeight(double width) {
        return computeMinPrefMaxHeight(width, Node::maxHeight);
    }

    private double computeMinPrefMaxHeight(double width, BiFunction<Node, Double, Double> minPrefMaxHeightFunction) {
        double minHeight = 0;
        List<Node> children = getManagedChildren();
        if (!children.isEmpty()) {
            int colCount = computeColumnCount(width);
            double widthNoGap = width - insetsWidth() - getHgap() * (colCount - 1);
            double w = width < 0 ? -1 : getColWidth(widthNoGap, colCount);
            for (int childIndex = 0; childIndex < children.size(); childIndex += colCount) {
                double rowMinPrefMaxHeight = computeRowMinPrefMaxHeight(children, childIndex, colCount, node -> minPrefMaxHeightFunction.apply(node, w));
                if (minHeight > 0)
                    rowMinPrefMaxHeight += getVgap();
                minHeight += rowMinPrefMaxHeight;
            }
        }
        return minHeight + insetsHeight();
    }
}
