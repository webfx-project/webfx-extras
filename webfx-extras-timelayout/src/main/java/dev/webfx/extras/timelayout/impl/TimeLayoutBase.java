package dev.webfx.extras.timelayout.impl;

import dev.webfx.extras.timelayout.LayoutBounds;
import dev.webfx.extras.timelayout.ChildTimeReader;
import dev.webfx.extras.timelayout.TimeLayout;
import dev.webfx.extras.timelayout.util.DirtyMarker;
import javafx.beans.property.*;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Bruno Salmon
 */
public abstract class TimeLayoutBase<C, T> extends ListenableTimeWindowImpl<T> implements TimeLayout<C, T> {

    private final DoubleProperty widthProperty = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
             markLayoutAsDirty();
        }
    };
    private final DoubleProperty heightProperty = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
             //markLayoutAsDirty(); // Commented as the layout actually doesn't depend on the height
        }
    };

    private final BooleanProperty visibleProperty = new SimpleBooleanProperty(true) {
        @Override
        protected void invalidated() {
            if (isVisible())
                markLayoutAsDirty();
        }
    };
    private final IntegerProperty layoutCountProperty = new SimpleIntegerProperty();

    protected final ObservableList<C> children = FXCollections.observableArrayList();
    private ChildTimeReader<C, T> childStartTimeReader = c -> (T) c;
    private boolean childStartTimeExclusive;
    private ChildTimeReader<C, T> childEndTimeReader = c -> (T) c;
    private boolean childEndTimeExclusive;
    protected List<LayoutBounds> childrenPositions;
    private int rowsCount;
    private double childFixedHeight = -1;
    private boolean fillHeight;
    private double topY;
    private double hSpacing = 0;
    private double vSpacing = 0;
    private final DirtyMarker layoutDirtyMarker = new DirtyMarker(this::layout);
    private boolean childSelectionEnabled;
    private ObjectProperty<C> selectedChildProperty;
    protected TimeProjector<T> timeProjector;

    public TimeLayoutBase() {
        children.addListener(this::onChildrenChanged);
        setOnTimeWindowChanged((end, start) -> markLayoutAsDirty());
    }


    protected void onChildrenChanged(ListChangeListener.Change<? extends C> c) {
        childrenPositions = Stream.generate(LayoutBounds::new)
                .limit(children.size())
                .collect(Collectors.toList());
        markLayoutAsDirty();
    }

    @Override
    public DoubleProperty widthProperty() {
        return widthProperty;
    }

    @Override
    public DoubleProperty heightProperty() {
        return heightProperty;
    }

    @Override
    public BooleanProperty visibleProperty() {
        return visibleProperty;
    }

    @Override
    public ObservableList<C> getChildren() {
        return children;
    }

    @Override
    public void setChildStartTimeReader(ChildTimeReader<C, T> startTimeReader, boolean exclusive) {
        this.childStartTimeReader = startTimeReader;
        childStartTimeExclusive = exclusive;
    }

    @Override
    public void setChildEndTimeReader(ChildTimeReader<C, T> childEndTimeReader, boolean exclusive) {
        this.childEndTimeReader = childEndTimeReader;
        childEndTimeExclusive = exclusive;
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
    public void markLayoutAsDirty() {
        if (!isLayouting())
            layoutDirtyMarker.markAsDirty();
    }

    @Override
    public boolean isLayoutDirty() {
        return layoutDirtyMarker.isDirty();
    }

    @Override
    public ObservableIntegerValue layoutCountProperty() {
        return layoutCountProperty;
    }

    @Override
    public void layout() {
        if (isVisible() && childrenPositions != null) {
            int newLayoutCount = getLayoutCount() + 1;
            layoutCountProperty.set(-newLayoutCount); // may trigger onBeforeLayout runnable(s)
            rowsCount = -1;
            onBeforeLayoutChildren();
            childrenPositions.forEach(p -> p.setValid(false));
            // Automatically updating the heights
            // 1) on fillHeight mode, we update the heights of the children, so they fit in the layout height
            if (fillHeight) {
                if (getRowsCount() > 0) {
                    double rowHeight = (getHeight() - topY) / rowsCount;
                    childrenPositions.forEach(p -> {
                        p.setY(topY + p.getRowIndex() * rowHeight);
                        p.setHeight(rowHeight);
                    });
                }
            }
            // 2) Otherwise a fixed height has been set on children, we compute the new height
            else if (!heightProperty.isBound())
                setHeight(computeHeight());
            layoutDirtyMarker.markAsClean();
            layoutCountProperty.set(newLayoutCount); // may trigger onAfterLayout runnable(s)
        }
    }

    protected double computeHeight() {
        int rowsCount = getRowsCount();
        return rowsCount == 0 ? 0 : rowsCount * (childFixedHeight + vSpacing) - vSpacing;
    }

    @Override
    public int getRowsCount() {
        if (rowsCount == -1) {
            rowsCount = 0;
            for (int i = 0; i < childrenPositions.size(); i++) {
                LayoutBounds cp = getChildPosition(i);
                int rowIndex = cp.getRowIndex();
                rowsCount = Math.max(rowsCount, rowIndex + 1);
            }
        }
        return rowsCount;
    }

    @Override
    public LayoutBounds getChildPosition(int childIndex) {
        LayoutBounds cp = childrenPositions.get(childIndex);
        if (!cp.isValid())
            updateChildPosition(childIndex, cp);
        return cp;
    }

    private T readChildStartTime(C child) {
        return childStartTimeReader.getTime(child);
    }

    private T readChildEndTime(C child) {
        return childEndTimeReader.getTime(child);
    }

    public TimeProjector<T> getTimeProjector() {
        return timeProjector;
    }

    public void setTimeProjector(TimeProjector<T> timeProjector) {
        this.timeProjector = timeProjector;
    }

    // Empty default implementation but can be override (ex: GanttLayout)
    protected void onBeforeLayoutChildren() { }

    protected void updateChildPosition(int childIndex, LayoutBounds cp) {
        C child = children.get(childIndex);
        T startTime = readChildStartTime(child);
        T endTime = readChildEndTime(child);
        TimeProjector<T> timeProjector = getTimeProjector();
        double startX = timeProjector.timeToX(startTime, true, childStartTimeExclusive);
        double endX = timeProjector.timeToX(endTime, false, childEndTimeExclusive);
        cp.setX(startX + hSpacing / 2);
        cp.setWidth(endX - startX - hSpacing);
        if (childFixedHeight > 0)
            cp.setHeight(childFixedHeight); // will be reset by layout if fillHeight is true
        updateChildPositionExtended(childIndex, cp, child, startTime, endTime, startX, endX);
        cp.setValid(true);
    }

    protected void updateChildPositionExtended(int childIndex, LayoutBounds cp, C child, T startTime, T endTime, double startX, double endX) {
        int columnIndex = computeChildColumnIndex(childIndex, child, startTime, endTime, startX, endX);
        int rowIndex = computeChildRowIndex(childIndex, child, startTime, endTime, startX, endX);
        cp.setRowIndex(rowIndex);
        cp.setColumnIndex(columnIndex);
        if (childFixedHeight > 0) {
            cp.setY(topY + (childFixedHeight + vSpacing) * rowIndex); // will be reset by layout if fillHeight is true
        }
    }

    protected int computeChildColumnIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX) {
        return 0;
    }

    protected int computeChildRowIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX) {
        return 0;
    }

    @Override
    public boolean isChildSelectionEnabled() {
        return childSelectionEnabled;
    }

    @Override
    public void setChildSelectionEnabled(boolean childSelectionEnabled) {
        this.childSelectionEnabled = childSelectionEnabled;
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
    public C pickChildAt(double x, double y, boolean onlyIfSelectable) {
        if (onlyIfSelectable && !isChildSelectionEnabled())
            return null;
        for (int i = 0; i < children.size(); i++) {
            LayoutBounds cp = getChildPosition(i);
            if (x >= cp.getX() && x <= cp.getX() + cp.getWidth() && y >= cp.getY() && y <= cp.getY() + cp.getHeight())
                return children.get(i);
        }
        return null;
    }

}
