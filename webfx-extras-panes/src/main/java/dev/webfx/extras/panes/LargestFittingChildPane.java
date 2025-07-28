package dev.webfx.extras.panes;

import dev.webfx.extras.util.layout.Layouts;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * A layout container that displays only one of its children at a time,
 * selecting the child with the largest width that fits within the container's current width.
 *
 * @author Bruno Salmon
 */
public class LargestFittingChildPane extends StackPane {

    // Optimization fields:
    private boolean layoutPass; // used to detect if the layout pass is new, or still the same
    private double lastWidth; // used to keep the same selection during the same layout pass
    private double smallestChildWidth;
    private double largestChildWidth;
    // The best child candidate to display at this time:
    private Node largestFittingChild;

    public LargestFittingChildPane(Node... children) {
        super(children);
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }

    private void selectLargestFittingChild(double width, boolean apply) {
        if (width > 0) {
            if (!layoutPass) { // New layout pass detection
                layoutPass = true;
                Platform.runLater(() -> layoutPass = false);
            } else if (lastWidth == width) // Same width in same layout pass => no change
                return;
            lastWidth = width;
        }
        //Console.log("width = " + width);
        largestFittingChild = null;
        double largestFittingChildWidth = smallestChildWidth = largestChildWidth = 0; // in case the children are empty
        for (Node child : getChildren()) {
            double childWidth = child.prefWidth(-1);
            if (largestFittingChild == null) {
                largestFittingChild = child;
                largestFittingChildWidth = smallestChildWidth = largestChildWidth = childWidth;
            } else if (childWidth <= width && (largestFittingChildWidth > width || childWidth > largestFittingChildWidth)) {
                largestFittingChild = child;
                largestFittingChildWidth = childWidth;
            }
            if (childWidth < smallestChildWidth) {
                smallestChildWidth = childWidth;
            } else if (childWidth > largestChildWidth) {
                largestChildWidth = childWidth;
            }
        }
        if (apply) {
            for (Node child : getChildren()) {
                Layouts.setManagedAndVisibleProperties(child, largestFittingChild == child);
            }
        }
    }

    @Override
    protected double computeMinWidth(double height) {
        selectLargestFittingChild(getWidth(), false);
        return smallestChildWidth;
    }

    @Override
    protected double computePrefWidth(double height) {
        selectLargestFittingChild(getWidth(), false);
        return largestChildWidth;
    }

    @Override
    protected double computeMaxWidth(double height) {
        selectLargestFittingChild(getWidth(), false);
        return largestChildWidth;
    }

    @Override
    protected double computeMinHeight(double width) {
        selectLargestFittingChild(width, true);
        return largestFittingChild == null ? 0 : largestFittingChild.minHeight(width);
    }

    @Override
    protected double computePrefHeight(double width) {
        selectLargestFittingChild(width, true);
        return largestFittingChild == null ? 0 : largestFittingChild.prefHeight(width);
    }

    @Override
    protected double computeMaxHeight(double width) {
        selectLargestFittingChild(width, true);
        return largestFittingChild == null ? 0 : largestFittingChild.maxHeight(width);
    }

}
