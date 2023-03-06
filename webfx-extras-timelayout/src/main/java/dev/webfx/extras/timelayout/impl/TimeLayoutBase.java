package dev.webfx.extras.timelayout.impl;

import dev.webfx.extras.timelayout.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Bruno Salmon
 */
public abstract class TimeLayoutBase<T, C> implements TimeLayout<T, C> {

    protected ObservableList<C> children = FXCollections.observableArrayList();
    protected ChildTimeReader<T, C> childTimeReader;
    protected List<ChildPosition<T>> childrenTimePositions;
    protected T timeWindowStart;
    protected T timeWindowEnd;
    protected TimeScale timeColumnScale = TimeScale.DAY;
    protected List<TimeColumn<T>> columns = new ArrayList<>();
    protected List<TimeRow> rows = new ArrayList<>();
    protected TimeCell<T>[][] cells;
    private int rowsCount;
    protected double layoutWidth, layoutHeight;
    private double childFixedHeight = -1;
    private boolean fillHeight;
    private double spacing = 2;

    public TimeLayoutBase() {
        children.addListener((ListChangeListener<C>) c ->
                childrenTimePositions = Stream.generate(ChildPosition<T>::new)
                    .limit(children.size())
                    .collect(Collectors.toList()));
    }

    @Override
    public ObservableList<C> getChildren() {
        return children;
    }

    @Override
    public void setChildTimeReader(ChildTimeReader<T, C> childTimeReader) {
        this.childTimeReader = childTimeReader;
    }

    @Override
    public void setTimeWindow(T timeWindowStart, T timeWindowEnd) {
        this.timeWindowStart = timeWindowStart;
        this.timeWindowEnd = timeWindowEnd;
    }

    @Override
    public void setTimeColumnScale(TimeScale timeColumnScale) {
        this.timeColumnScale = timeColumnScale;
    }

    @Override
    public double getChildFixedHeight() {
        return childFixedHeight;
    }

    @Override
    public void setChildFixedHeight(double childFixedHeight) {
        this.childFixedHeight = childFixedHeight;
    }

    @Override
    public boolean isFillHeight() {
        return fillHeight;
    }

    @Override
    public void setFillHeight(boolean fillHeight) {
        this.fillHeight = fillHeight;
    }

    @Override
    public void layout(double width, double height) {
        if (layoutWidth != width || layoutHeight != height) {
            layoutWidth = width;
            layoutHeight = height;
            rowsCount = -1;
            childrenTimePositions.forEach(p -> p.setValid(false));
            if (fillHeight && getRowsCount() > 0) {
                double rowHeight = height / rowsCount;
                childrenTimePositions.forEach(p -> {
                    p.setY(p.getRowIndex() * rowHeight);
                    p.setHeight(rowHeight);
                });
            }
        }
    }

    @Override
    public List<TimeColumn<T>> getColumns() {
        return columns;
    }

    @Override
    public List<TimeRow> getRows() {
        return rows;
    }

    @Override
    public int getRowsCount() {
        if (rowsCount == -1) {
            rowsCount = 0;
            for (int i = 0; i < childrenTimePositions.size(); i++) {
                ChildPosition<T> childPosition = getChildPosition(i);
                int rowIndex = childPosition.getRowIndex();
                rowsCount = Math.max(rowsCount, rowIndex + 1);
            }
        }
        return rowsCount;
    }

    @Override
    public ChildPosition<T> getChildPosition(int childIndex) {
        ChildPosition<T> childPosition = childrenTimePositions.get(childIndex);
        if (!childPosition.isValid())
            updateChildPosition(childIndex, childPosition);
        return childPosition;
    }

    protected void updateChildPosition(int childIndex, ChildPosition<T> childPosition) {
        C child = children.get(childIndex);
        T startTime = childTimeReader.getStartTime(child);
        T endTime = childTimeReader.getEndTime(child);
        TimeProjector<T> timeProjector = getTimeProjector();
        double startX = timeProjector.timeToX(startTime, false, layoutWidth);
        double endX = timeProjector.timeToX(endTime, true, layoutWidth);
        int columnIndex = computeChildColumnIndex(childIndex, child, startTime, endTime, startX, endX);
        int rowIndex = computeChildRowIndex(childIndex, child, startTime, endTime, startX, endX);
        TimeCell<T> cell = null; //cells[rowIndex][columnIndex];
        childPosition.setOriginCell(cell);
        childPosition.setRowIndex(rowIndex);
        childPosition.setColumnIndex(columnIndex);
        childPosition.setX(startX + spacing / 2);
        childPosition.setWidth(endX - startX - spacing);
        if (childFixedHeight > 0) {
            childPosition.setHeight(childFixedHeight - spacing); // will be reset by layout if fillHeight is true
            childPosition.setY(childFixedHeight * rowIndex + spacing / 2); // will be reset by layout if fillHeight is true
        } else {
        }
        childPosition.setValid(true);
    }

    protected abstract TimeProjector<T> getTimeProjector();

    protected abstract int computeChildColumnIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX);

    protected abstract int computeChildRowIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX);

}
