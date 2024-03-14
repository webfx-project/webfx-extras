package dev.webfx.extras.panes;

import dev.webfx.platform.util.Arrays;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public class FlexColumnPane extends Pane implements Scalable {

    private final static double hGap = 15, vGap = 15;
    private final static double minZoomFactorJustifyingColumnIncrease = 1.15;
    private final static double unscaledColumnWidth = 300;
    private double additionalContentHeight;
    private ScaleComputer scaleComputer;
    private double maxX, maxY;
    private ChildInfo[] childInfos;
    double[] childHeights;
    private double colWidthOnLastChildHeightsComputation;

    public FlexColumnPane() {
    }

    public FlexColumnPane(Node... children) {
        super(children);
    }

    @Override
    public void prepareScale(double additionalContentHeight, ScaleComputer scaleComputer) {
        this.additionalContentHeight = additionalContentHeight;
        this.scaleComputer = scaleComputer;
        requestLayout(); // necessary?
    }

    @Override
    protected void layoutChildren() {
        computeLayout(getWidth(), getHeight(), true);
    }

    private void computeLayout(double width, double height, boolean apply) {
        List<Node> children = getManagedChildren();
        if (children.isEmpty())
            return;
        log("computeLayout(" + width + ", " + height + ", " + apply + ")");
        double bestComputedScale = -1;
        int bestComputedScaleColCount = 0;
        double colFactor = 1;
        for (int colCount = 1; colCount < children.size(); colCount++) {
            boolean fit = computeFixedColumnLayout(width, height, colCount, children, apply);
            if (scaleComputer == null) {
                if (fit)
                    break;
            } else {
                double computedScale = scaleComputer.computeScale(maxX, maxY + additionalContentHeight);
                computedScale /= colFactor;
                if (fit && (bestComputedScale < 0 || computedScale > bestComputedScale)) {
                    bestComputedScale = computedScale;
                    bestComputedScaleColCount = colCount;
                }
            }
            colFactor *= minZoomFactorJustifyingColumnIncrease;
        }
        if (scaleComputer != null) {
            log("colCount = " + bestComputedScaleColCount + ", bestScale = " + bestComputedScale);
            //computeFixedColumnLayout(bestComputedScaleHeight * bestComputedScale, bestComputedScaleHeight, bestComputedScaleColCount, children, apply);
            computeFixedColumnLayout(width, height, bestComputedScaleColCount, children, apply);
        }
        if (apply) {
            for (ChildInfo ci : childInfos) {
                layoutInArea(ci.child, ci.x, ci.y, ci.w, ci.h, 0, HPos.CENTER, VPos.CENTER);
            }
        }
    }

    private boolean computeFixedColumnLayout(double width, double height, int colCount, List<Node> children, boolean apply) {
        double colWidth = (width - hGap * (colCount - 1)) / colCount;
        if (scaleComputer != null && !apply) {
            colWidth = unscaledColumnWidth;
        }

        int n = children.size();
        if (childHeights == null || childHeights.length != n || colWidthOnLastChildHeightsComputation != colWidth) {
            childHeights = new double[n];
            for (int i = 0; i < n; i++) {
                Node child = children.get(i);
                childHeights[i] = child.prefHeight(colWidth);
            }
            colWidthOnLastChildHeightsComputation = colWidth;
        }

        List<Integer> splitPoints = splitIntoColumns(childHeights, colCount);
        if (splitPoints.size() != colCount)
            return false;

        if (childInfos == null || childInfos.length != n) {
            childInfos = new ChildInfo[n];
        }

        maxY = 0;
        double x = 0, y = 0;
        int i = 0;
        for (int j : splitPoints) {
            // Filling the column
            while (i <= j) {
                Node child = children.get(i);
                ChildInfo ci = childInfos[i];
                if (ci == null || ci.child != child) {
                    ci = childInfos[i] = new ChildInfo(child, x, y, colWidth, childHeights[i]);
                } else {
                    ci.x = x;
                    ci.y = y;
                    ci.w = colWidth;
                    ci.h = childHeights[i];
                }
                i++;
                y += ci.h;
                if (y > maxY)
                    maxY = y;
                y += vGap;
            }
            // Preparing the position for the next column
            x += colWidth + hGap;
            y = 0;
        }
        maxX = x;

        if (apply)
            return maxY <= height;

        return true;
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
        private double x, y, w, h;

        public ChildInfo(Node child, double x, double y, double w, double h) {
            this.child = child;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

    }

    public static List<Integer> splitIntoColumns(double[] childHeights, int colCount) {
        List<Integer> result = new ArrayList<>();
        if (colCount == 1) {
            result.add(childHeights.length - 1);
            return result;
        }
        double low = 0, high = Arrays.sum(childHeights);

        while (low < high) {
            double mid = (low + high) / 2;
            if (canSplit(childHeights, colCount, mid)) {
                high = mid;
                result.clear();
                result.addAll(findSplitPoints(childHeights, mid));
            } else {
                low = mid + 1;
            }
        }

        return result;
    }

    private static boolean canSplit(double[] childHeights, int colCount, double maxSum) {
        int count = 0;
        double sum = 0;

        for (double h : childHeights) {
            sum += h;
            if (sum > maxSum) {
                sum = 0;
                count++;
            }
        }

        return count < colCount;
    }

    private static List<Integer> findSplitPoints(double[] childHeights, double maxSum) {
        List<Integer> splitPoints = new ArrayList<>();
        double sum = 0;
        int lastSplitIndex = -1;

        for (int i = 0; i < childHeights.length; i++) {
            sum += childHeights[i];
            if (sum > maxSum) {
                sum = 0;
                splitPoints.add(i);
                lastSplitIndex = i;
            }
        }

        if (lastSplitIndex != childHeights.length - 1) {
            splitPoints.add(childHeights.length - 1);
        }

        return splitPoints;
    }

}
