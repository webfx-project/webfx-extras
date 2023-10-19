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
public class FlexColumnPane extends Pane implements HasPrefRatio {

    private double prefRatio = -1;

    public FlexColumnPane() {
    }

    public FlexColumnPane(Node... children) {
        super(children);
        //setBackground(Background.fill(Color.LIGHTPINK));
    }

    @Override
    public Orientation getContentBias() {
        return null; // Orientation.HORIZONTAL;
    }

    @Override
    public void setPrefRatio(double prefRatio) {
        if (prefRatio != this.prefRatio) {
            this.prefRatio = prefRatio;
            requestLayout();
        }
    }

    @Override
    protected void layoutChildren() {
        computeLayout(getWidth(), getHeight(), true);
    }

    private double maxX, maxY;
    private int lastColChildCount;
    private ChildInfo[] childInfos;

    private void computeLayout(double width, double height, boolean apply) {
        List<Node> children = getManagedChildren();
        if (children.isEmpty())
            return;
        log("computeLayout(" + width + ", " + height + ", " + apply + ")");
        double bestComputedRatio = -1, bestComptedRationHeight = -1;
        int bestComputedRatioColCount = 0;
        int lastColChildCount = 0;
        for (int colCount = 1; colCount < children.size() ; colCount++) {
            boolean fit = computeFixedColumnLayout(width, height, colCount, children, apply);
            /*if (this.lastColChildCount == 1 && children.size() > 2)
                fit = false;*/
            if (prefRatio <= 0) {
                if (fit)
                    break;
            } else {
                double computedRatio = maxX / maxY;
                if (fit && (bestComputedRatio < 0 || isBetterRatio(computedRatio, bestComputedRatio))) {
                    bestComputedRatio = computedRatio;
                    bestComputedRatioColCount = colCount;
                    bestComptedRationHeight = maxY;
                    lastColChildCount = this.lastColChildCount;
                }
            }
        }
        if (prefRatio > 0) {
            log("colCount = " + bestComputedRatioColCount + ", bestRatio = " + bestComputedRatio + " (requested prefRatio = " + prefRatio + ") - lastColChildCount = " + lastColChildCount);
            computeFixedColumnLayout(bestComptedRationHeight * bestComputedRatio, bestComptedRationHeight, bestComputedRatioColCount, children, apply);
        }
        if (apply) {
            for (ChildInfo ci : childInfos) {
                layoutInArea(ci.child, ci.x, ci.y, ci.w, ci.h, 0, HPos.CENTER, VPos.CENTER);
            }
        }
    }

    private boolean isBetterRatio(double ratio1, double ratio2) {
        double d1 = ratio1 < prefRatio ? prefRatio - ratio1 : 1 * (ratio1 - prefRatio);
        double d2 = ratio2 < prefRatio ? prefRatio - ratio2 : 1 * (ratio2 - prefRatio);
        return d1 < d2;
    }

    private final double hGap = 15, vGap = 15;

    private boolean computeFixedColumnLayout(double width, double height, int colCount, List<Node> children, boolean apply) {
        if (childInfos == null || childInfos.length != children.size()) {
            childInfos = new ChildInfo[children.size()];
        }
        double colWidth = (width - hGap * (colCount - 1)) / colCount;
        if (prefRatio > 0 && !apply) {
            colWidth = 200;
            width = colWidth * colCount + hGap * (colCount - 1);
            height = width / prefRatio;
        }
        ComputeRound cr = new ComputeRound(colCount);
        while (true) {
            executeComputeRound(cr, width, height, colWidth, children);
            boolean fit = cr.colIndex == colCount - 1;
            if (apply || fit || cr.colIndex < colCount - 1) {
                maxX = cr.maxX;
                maxY = cr.maxY;
                lastColChildCount = cr.lastColChildCount;
                if (!apply && fit && prefRatio > 0 && colCount > 1) {
                    double ratio = maxX / maxY;
                    if (ratio > prefRatio) {
                        cr.increaseSmallestColumn();
                        executeComputeRound(cr, width, height, colWidth, children);
                        if (cr.colIndex == colCount -1) {
                            double newRatio = cr.maxX / cr.maxY;
                            log(ratio + " -> " + newRatio + " for " + prefRatio + "? " + isBetterRatio(newRatio, ratio));
                            if (isBetterRatio(newRatio, ratio)) {
                                maxX = cr.maxX;
                                maxY = cr.maxY;
                                lastColChildCount = cr.lastColChildCount;
                            }
                        }
                    }
                }
                return fit;
            }
            cr.increaseSmallestColumn();
        }
    }

    private void executeComputeRound(ComputeRound cr, double width, double height, double colWidth, List<Node> children) {
        double x = 0, y = 0;
        cr.maxX = cr.maxY = 0;
        cr.smallestNewIncreaseHeight = -1;
        cr.smallestNewIncreaseColumnIndex = -1;
        cr.colIndex = 0;
        cr.lastColChildCount = 0;
        int childIndex = 0, increaseColumn = cr.increaseColumns[cr.colIndex];
        for (Node child : children) {
            double w = colWidth;
            double h = child.prefHeight(w);
            double bottom = y + h;
            if (width > 0 && height > 0 && bottom > height) {
                if (increaseColumn > 0)
                    increaseColumn--;
                else {
                    if (++cr.colIndex >= cr.colCount)
                        break;
                    if (cr.colIndex < cr.colCount - 1 && (cr.smallestNewIncreaseHeight < 0 || bottom < cr.smallestNewIncreaseHeight)) {
                        cr.smallestNewIncreaseHeight = bottom;
                        cr.smallestNewIncreaseColumnIndex = cr.colIndex;
                    }
                    increaseColumn = cr.increaseColumns[cr.colIndex];
                    x += w + hGap;
                    y = 0;
                }
            }
            if (cr.colIndex == cr.colCount - 1)
                cr.lastColChildCount++;
            childInfos[childIndex] = new ChildInfo(child, x, y, w, h);
            cr.maxX = Math.max(cr.maxX, x + w);
            cr.maxY = Math.max(cr.maxY, y + h);
            y += h + vGap;
            childIndex++;
        }
    }

    @Override
    protected double computePrefWidth(double height) {
        double prefWidth;
        computeLayout(-1, height, false);
        prefWidth = maxX;
        log("computePrefWidth(" + height + ") = " + prefWidth);
        return prefWidth;
    }

    @Override
    protected double computePrefHeight(double width) {
        double prefHeight;
        computeLayout(width, -1, false);
        prefHeight = maxY;
        log("computePrefHeight(" + width + ") = " + prefHeight);
        return prefHeight;
    }

    private void log(String message) {
        System.out.println(message);
    }

    private static final class ChildInfo {
        private final Node child;
        private final double x, y, w, h;

        public ChildInfo(Node child, double x, double y, double w, double h) {
            this.child = child;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        @Override
        public String toString() {
            return "ChildInfo{" +
                    "child=" + child +
                    ", x=" + x +
                    ", y=" + y +
                    ", w=" + w +
                    ", h=" + h +
                    '}';
        }
    }

    private static final class ComputeRound {
        private final int colCount;
        private int colIndex;
        private int[] increaseColumns;
        private int smallestNewIncreaseColumnIndex;
        private double smallestNewIncreaseHeight;
        double maxX, maxY;
        int lastColChildCount;

        public ComputeRound(int colCount) {
            this.colCount = colCount;
            increaseColumns = new int[colCount];
        }

        private void increaseSmallestColumn() {
            int newIncreaseColumnIndex = Math.max(0, smallestNewIncreaseColumnIndex);
            increaseColumns[newIncreaseColumnIndex]++;
            for (int i = newIncreaseColumnIndex + 1; i < increaseColumns.length; i++)
                increaseColumns[i] = 0;
        }
    }
}
