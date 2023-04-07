package dev.webfx.extras.timelayout.impl;

import dev.webfx.extras.timelayout.*;
import dev.webfx.extras.timelayout.util.TimeUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
public abstract class TimeLayoutBase<C, T> implements TimeLayout<C, T> {

    protected final ObservableList<C> children = FXCollections.observableArrayList();
    protected ChildTimeReader<C, T> childTimeReader = (ChildTimeReader) TimeUtil.<T>immediateChildTimeReader();
    protected List<ChildPosition<T>> childrenTimePositions;
    protected T timeWindowStart;
    protected T timeWindowEnd;
    protected List<TimeColumn<T>> columns = new ArrayList<>();
    protected List<TimeRow> rows = new ArrayList<>();
    protected TimeCell<T>[][] cells;
    private int rowsCount;
    protected double layoutWidth, layoutHeight;
    private double childFixedHeight = -1;
    private boolean fillHeight;
    private double topY;
    private double hSpacing = 0;
    private double vSpacing = 0;

    private ObjectProperty<C> selectedChildProperty;

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
    public void setChildTimeReader(ChildTimeReader<C, T> childTimeReader) {
        this.childTimeReader = childTimeReader;
    }

    @Override
    public void setTimeWindow(T timeWindowStart, T timeWindowEnd) {
        this.timeWindowStart = timeWindowStart;
        this.timeWindowEnd = timeWindowEnd;
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
    public double getTopY() {
        return topY;
    }

    @Override
    public void setTopY(double topY) {
        this.topY = topY;
    }

    @Override
    public double getHSpacing() {
        return hSpacing;
    }

    @Override
    public void setHSpacing(double hSpacing) {
        this.hSpacing = hSpacing;
    }

    @Override
    public double getVSpacing() {
        return vSpacing;
    }

    @Override
    public void setVSpacing(double vSpacing) {
        this.vSpacing = vSpacing;
    }

    @Override
    public void layout(double width, double height) {
        if ((layoutWidth != width || layoutHeight != height) && childrenTimePositions != null) {
            layoutWidth = width;
            layoutHeight = height;
            rowsCount = -1;
            childrenTimePositions.forEach(p -> p.setValid(false));
            if (fillHeight && getRowsCount() > 0) {
                double rowHeight = (height - topY) / rowsCount;
                childrenTimePositions.forEach(p -> {
                    p.setY(topY + p.getRowIndex() * rowHeight);
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
        double startX = timeProjector.timeToX(startTime, true, false, layoutWidth);
        double endX = timeProjector.timeToX(endTime, false, false, layoutWidth);
        int columnIndex = computeChildColumnIndex(childIndex, child, startTime, endTime, startX, endX);
        int rowIndex = computeChildRowIndex(childIndex, child, startTime, endTime, startX, endX);
        TimeCell<T> cell = null; //cells[rowIndex][columnIndex];
        childPosition.setOriginCell(cell);
        childPosition.setRowIndex(rowIndex);
        childPosition.setColumnIndex(columnIndex);
        childPosition.setX(startX + hSpacing / 2);
        childPosition.setWidth(endX - startX - hSpacing);
        if (childFixedHeight > 0) {
            childPosition.setHeight(childFixedHeight - vSpacing); // will be reset by layout if fillHeight is true
            childPosition.setY(topY + childFixedHeight * rowIndex + vSpacing / 2); // will be reset by layout if fillHeight is true
        } else {
        }
        childPosition.setValid(true);
    }

    protected abstract TimeProjector<T> getTimeProjector();

    protected abstract int computeChildColumnIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX);

    protected abstract int computeChildRowIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX);

    @Override
    public void setSelectedChild(C child) {
        if (selectedChildProperty != null && child == null)
            return;
        selectedChildProperty().set(child);
    }

    @Override
    public C getSelectedChild() {
        return selectedChildProperty == null ? null : selectedChildProperty.get();
    }

    @Override
    public ObjectProperty<C> selectedChildProperty() {
        if (selectedChildProperty == null)
            selectedChildProperty = new SimpleObjectProperty<>();
        return selectedChildProperty;
    }

    @Override
    public C pickChild(double x, double y) {
        for (int i = 0; i < children.size(); i++) {
            ChildPosition<T> cp = getChildPosition(i);
            if (x >= cp.getX() && x <= cp.getX() + cp.getWidth() && y >= cp.getY() && y <= cp.getY() + cp.getHeight())
                return children.get(i);
        }
        return null;
    }
}
