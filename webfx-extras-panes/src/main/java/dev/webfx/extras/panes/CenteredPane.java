package dev.webfx.extras.panes;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * Draft version (no top, no bottom, no margins, etc...)
 *
 * @author Bruno Salmon
 */
public final class CenteredPane extends Pane {

    private final ObjectProperty<Node> leftProperty = new NodePositionProperty("left");
    private final ObjectProperty<Node> centerProperty = new NodePositionProperty("center");
    private final ObjectProperty<Node> rightProperty = new NodePositionProperty("right");

    public CenteredPane() {
        super();
        setMaxWidth(Double.MAX_VALUE);
    }

    public Node getLeft() {
        return leftProperty.get();
    }

    public void setLeft(Node left) {
        leftProperty.set(left);
    }

    public ObjectProperty<Node> left() {
        return leftProperty;
    }

    public Node getCenter() {
        return centerProperty.get();
    }

    public void setCenter(Node center) {
        centerProperty.set(center);
    }

    public ObjectProperty<Node> centerProperty() {
        return centerProperty;
    }

    public Node getRight() {
        return rightProperty.get();
    }

    public void setRight(Node right) {
        rightProperty.set(right);
    }

    public ObjectProperty<Node> rightProperty() {
        return rightProperty;
    }

    /**
     * @return null unless the center, right, bottom, left or top has a content bias.
     */
    @Override public Orientation getContentBias() {
        final Node c = getCenter();
        if (c != null && c.isManaged() && c.getContentBias() != null) {
            return c.getContentBias();
        }

        final Node r = getRight();
        if (r != null && r.isManaged() && r.getContentBias() == Orientation.VERTICAL) {
            return r.getContentBias();
        }

        final Node l = getLeft();
        if (l != null && l.isManaged() && l.getContentBias() == Orientation.VERTICAL) {
            return l.getContentBias();
        }

/*
        final Node b = getBottom();
        if (b != null && b.isManaged() && b.getContentBias() == Orientation.HORIZONTAL) {
            return b.getContentBias();
        }

        final Node t = getTop();
        if (t != null && t.isManaged() && t.getContentBias() == Orientation.HORIZONTAL) {
            return t.getContentBias();
        }
*/

        return null;
    }


    @Override
    protected void layoutChildren() { // Draft version
        double width = getWidth(), height = getHeight();
        Node left = getLeft(), center = getCenter(), right = getRight();
        double lw = left == null ? 0 : left.prefWidth(height);
        double rw = right == null ? 0 : right.prefWidth(height);
        double cw = center == null ? 0 : center.prefWidth(height);
        if (left != null)
            layoutInArea(left, 0, 0, lw, height, 0, HPos.LEFT, VPos.CENTER);
        double rx = width - rw;
        if (right != null)
            layoutInArea(right, rx, 0, rw, height, 0, HPos.LEFT, VPos.CENTER);
        if (center != null) {
            if (lw + cw + rw > width)
                cw = Math.max(0, width - lw - rw);
            double cx = width / 2 - cw / 2; // Ideally
            if (cx < lw) {
                cx = lw;
            } else if (cx + cw > rx) {
                cx = rx - cw;
            }
            layoutInArea(center, cx, 0, cw, height, 0, HPos.LEFT, VPos.CENTER);
        }
    }

    @Override
    protected double computeMinWidth(double height) {
        return computeChildMinWidth(getLeft(), height) + computeChildMinWidth(getCenter(), height) + computeChildMinWidth(getRight(), height);
    }

    @Override
    protected double computeMinHeight(double width) {
        return Math.max(computeChildMinHeight(getLeft(), width), Math.max(computeChildMinHeight(getCenter(), width), computeChildMinHeight(getRight(), width)));
    }

    @Override
    protected double computePrefWidth(double height) {
        return computeChildPrefWidth(getLeft(), height) + computeChildPrefWidth(getCenter(), height) + computeChildPrefWidth(getRight(), height);
    }

    @Override
    protected double computePrefHeight(double width) {
        return Math.max(computeChildPrefHeight(getLeft(), width), Math.max(computeChildPrefHeight(getCenter(), width), computeChildPrefHeight(getRight(), width)));
    }

    @Override
    protected double computeMaxWidth(double height) {
        return computeChildMaxWidth(getLeft(), height) + computeChildMaxWidth(getCenter(), height) + computeChildMaxWidth(getRight(), height);
    }

    @Override
    protected double computeMaxHeight(double width) {
        return Math.max(computeChildMaxHeight(getLeft(), width), Math.max(computeChildMaxHeight(getCenter(), width), computeChildMaxHeight(getRight(), width)));
    }

    private static double computeChildMinWidth(Node child, double height) {
        return child == null ? 0 : child.minWidth(height);
    }

    private static double computeChildMinHeight(Node child, double width) {
        return child == null ? 0 : child.minHeight(width);
    }

    private static double computeChildPrefWidth(Node child, double height) {
        return child == null ? 0 : child.prefWidth(height);
    }

    private static double computeChildPrefHeight(Node child, double width) {
        return child == null ? 0 : child.prefHeight(width);
    }

    private static double computeChildMaxWidth(Node child, double height) {
        return child == null ? 0 : child.maxWidth(height);
    }

    private static double computeChildMaxHeight(Node child, double width) {
        return child == null ? 0 : child.maxHeight(width);
    }

    /* *************************************************************************
     *                                                                         *
     *                         Private Inner Class                             *
     *                                                                         *
     **************************************************************************/

    private final class NodePositionProperty extends ObjectPropertyBase<Node> {
        private Node oldValue = null;
        private final String propertyName;
        private boolean isBeingInvalidated;

        NodePositionProperty(String propertyName) {
            this.propertyName = propertyName;
            getChildren().addListener((ListChangeListener<Node>) c -> {
                if (oldValue == null || isBeingInvalidated) {
                    return;
                }
                while (c.next()) {
                    if (c.wasRemoved()) {
                        List<? extends Node> removed = c.getRemoved();
                        for (int i = 0, sz = removed.size(); i < sz; ++i) {
                            if (removed.get(i) == oldValue) {
                                oldValue = null; // Do not remove again in invalidated
                                set(null);
                            }
                        }
                    }
                }
            });
        }

        @Override
        protected void invalidated() {
            final List<Node> children = getChildren();

            isBeingInvalidated = true;
            try {
                if (oldValue != null) {
                    children.remove(oldValue);
                }

                final Node _value = get();
                this.oldValue = _value;

                if (_value != null) {
                    children.add(_value);
                }
            } finally {
                isBeingInvalidated = false;
            }
        }

        @Override
        public Object getBean() {
            return CenteredPane.this;
        }

        @Override
        public String getName() {
            return propertyName;
        }
    }

}
