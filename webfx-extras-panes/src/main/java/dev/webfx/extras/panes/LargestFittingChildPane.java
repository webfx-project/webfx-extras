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
    // The best child candidate to display at this time:
    private Node largestFittingChild;

    public LargestFittingChildPane(Node... children) {
        super(children);
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }

    private void selectLargestFittingChild(double width) {
        if (width <= 0)
            return;
        if (!layoutPass) { // New layout pass detection
            layoutPass = true;
            Platform.runLater(() -> layoutPass = false);
        } else if (lastWidth == width) // Same width in same layout pass => no change
            return;
        //Console.log("width = " + width);
        lastWidth = width;
        largestFittingChild = null;
        double bestCandidateWidth = 0;
        for (int i = 0, n = getChildren().size(); i < n; i++) {
            Node candidate = getChildren().get(i);
            double candidateWidth = candidate.prefWidth(-1);
            if (largestFittingChild == null) {
                largestFittingChild = candidate;
                bestCandidateWidth = candidateWidth;
            } else if (candidateWidth <= width && (bestCandidateWidth > width || candidateWidth > bestCandidateWidth)) {
                largestFittingChild = candidate;
                bestCandidateWidth = candidateWidth;
            }
        }
        for (Node child : getChildren()) {
            Layouts.setManagedAndVisibleProperties(child, largestFittingChild == child);
        }
    }

    @Override
    protected double computeMinWidth(double height) {
        selectLargestFittingChild(getWidth());
        return largestFittingChild == null ? 0 : largestFittingChild.minWidth(height);
    }

    @Override
    protected double computePrefWidth(double height) {
        selectLargestFittingChild(getWidth());
        return largestFittingChild == null ? 0 : largestFittingChild.prefWidth(height);
    }

    @Override
    protected double computeMaxWidth(double height) {
        selectLargestFittingChild(getWidth());
        return largestFittingChild == null ? 0 : largestFittingChild.maxWidth(height);
    }

    @Override
    protected double computeMinHeight(double width) {
        selectLargestFittingChild(width);
        return largestFittingChild == null ? 0 : largestFittingChild.minHeight(width);
    }

    @Override
    protected double computePrefHeight(double width) {
        selectLargestFittingChild(width);
        return largestFittingChild == null ? 0 : largestFittingChild.prefHeight(width);
    }

    @Override
    protected double computeMaxHeight(double width) {
        selectLargestFittingChild(width);
        return largestFittingChild == null ? 0 : largestFittingChild.maxHeight(width);
    }

}
