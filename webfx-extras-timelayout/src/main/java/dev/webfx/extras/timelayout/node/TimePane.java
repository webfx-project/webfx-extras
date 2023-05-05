package dev.webfx.extras.timelayout.node;

import dev.webfx.extras.timelayout.LayoutPosition;
import dev.webfx.extras.timelayout.TimeLayout;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * @author Bruno Salmon
 */
public class TimePane<C, T> extends Pane {

    private final TimeLayout<C, T> timeLayout;
    private final ChildNodeGetter<C> childNodeGetter;

    public TimePane(TimeLayout<C, T> timeLayout, ChildNodeGetter<C> childNodeGetter) {
        this.timeLayout = timeLayout;
        this.childNodeGetter = childNodeGetter;
        syncChildren();
        timeLayout.getChildren().addListener((ListChangeListener<C>) c -> syncChildren());
    }

    private void syncChildren() {
        getChildren().setAll(timeLayout.getChildren().stream().map(childNodeGetter::getNode).toArray(Node[]::new));
    }

    @Override
    protected void layoutChildren() {
        timeLayout.layout(getWidth(), getHeight());
        ObservableList<Node> children = getChildren();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            LayoutPosition p = timeLayout.getChildPosition(i);
            layoutInArea(child, p.getX(), p.getY(), p.getWidth(), p.getHeight(), 0, HPos.CENTER, VPos.CENTER);
        }
    }

    @Override
    protected double computePrefHeight(double width) {
        if (timeLayout.isFillHeight())
            return super.computePrefHeight(width);
        return timeLayout.getRowsCount() * timeLayout.getChildFixedHeight();
/*
        if (width == -1)
            width = getWidth();
        timeLayout.layout(width, getHeight());
        ObservableList<Node> children = getChildren();
        double prefHeight = 0, rowHeight = 0;
        int row = 0;
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            ChildPosition<T> childPosition = timeLayout.getChildPosition(i);
            if (childPosition.getRowIndex() != row) {
                prefHeight += rowHeight;
                rowHeight = 0;
                row = childPosition.getRowIndex();
            }
            rowHeight = Math.max(rowHeight, child.prefHeight(childPosition.getWidth()) + 200);
        }
        prefHeight += rowHeight;
        return prefHeight;
 */
    }
}
