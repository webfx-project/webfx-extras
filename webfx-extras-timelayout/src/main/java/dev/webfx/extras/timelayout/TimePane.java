package dev.webfx.extras.timelayout;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * @author Bruno Salmon
 */
public class TimePane<T, C> extends Pane {

    private final TimeLayout<T, C> timeLayout;
    private final ChildNodeGetter<C> childNodeGetter;

    public TimePane(TimeLayout<T, C> timeLayout, ChildNodeGetter<C> childNodeGetter) {
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
            ChildPosition<T> childPosition = timeLayout.getChildPosition(i);
            layoutInArea(child, childPosition.getX(), childPosition.getY(), childPosition.getWidth(), childPosition.getHeight(), 0, HPos.CENTER, VPos.CENTER);
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
