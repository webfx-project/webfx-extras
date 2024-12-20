package dev.webfx.extras.panes;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * This class fixes a bug in BorderPane with computeMinHeight() and computePrefHeight() methods which ignore the insets
 * when the center node has a horizontal bias which can cause endless layout passes, making the application very slow.
 *
 * @author Bruno Salmon
 */
public class PatchedBorderPane extends BorderPane {

    public PatchedBorderPane() {
    }

    public PatchedBorderPane(Node center) {
        super(center);
    }

    public PatchedBorderPane(Node center, Node top, Node right, Node bottom, Node left) {
        super(center, top, right, bottom, left);
    }

    @Override
    protected double computeMinHeight(double width) {
        // TODO: add an additional condition to check if the OpenJFX BorderPane still has the issue (it may be fixed in
        //  the future).
        if (width != -1)
            width -= getInsets().getLeft() + getInsets().getRight();
        return super.computeMinHeight(width);
    }

    @Override
    protected double computePrefHeight(double width) {
        // TODO: add an additional condition to check if the OpenJFX BorderPane still has the issue (it may be fixed in
        //  the future).
        if (width != -1)
            width -= getInsets().getLeft() + getInsets().getRight();
        return super.computePrefHeight(width);
    }

}
