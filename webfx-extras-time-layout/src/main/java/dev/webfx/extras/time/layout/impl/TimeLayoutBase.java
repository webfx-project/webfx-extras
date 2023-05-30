package dev.webfx.extras.time.layout.impl;

import dev.webfx.extras.geometry.Bounds;
import dev.webfx.extras.time.layout.TimeLayout;
import dev.webfx.extras.time.projector.TimeProjector;
import dev.webfx.extras.time.window.impl.ListenableTimeWindowImpl;
import dev.webfx.extras.util.DirtyMarker;
import javafx.beans.property.*;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Bruno Salmon
 */
public abstract class TimeLayoutBase<C, T> extends ListenableTimeWindowImpl<T> implements TimeLayout<C, T> {

    private final DoubleProperty widthProperty = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
             invalidateHorizontalLayout();
        }
    };
    private final DoubleProperty heightProperty = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            if (fillHeight)
                invalidateVerticalLayout();
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
    private Function<C, T> childStartTimeReader = c -> (T) c;
    private boolean childStartTimeExclusive;
    private Function<C, T> childEndTimeReader = c -> (T) c;
    private boolean childEndTimeExclusive;
    protected List<ChildBounds<C, T>> childrenBounds; // initially null until first children are set (to make difference between children not yet received and children received but empty)
    protected int rowsCount;
    private double childFixedHeight = -1;
    private boolean fillHeight;
    private double topY;
    private double hSpacing = 0;
    private double vSpacing = 0;
    private final DirtyMarker layoutDirtyMarker = new DirtyMarker(this::layout);
    private boolean childSelectionEnabled;
    private ObjectProperty<C> selectedChildProperty;
    protected TimeProjector<T> timeProjector;
    public int timeVersion, horizontalVersion, verticalVersion;

    public TimeLayoutBase() {
        children.addListener(this::onChildrenChanged);
        setOnTimeWindowChanged((end, start) -> invalidateHorizontalLayout()); // this changes the x values for the time projection (but children start/end times & y)
    }

    protected void onChildrenChanged(ListChangeListener.Change<? extends C> c) {
        // TODO: improve this and try to recycle existing layout bounds
        childrenBounds = Stream.generate(this::createChildLayoutBounds)
                .limit(children.size())
                .collect(Collectors.toList());
        for (int i = 0; i < children.size(); i++) {
            ChildBounds<C, T> cb = childrenBounds.get(i);
            cb.setObject(children.get(i));
        }
        invalidateTimesReading(); // the children may have new start & end times
    }

    protected ChildBounds<C, T> createChildLayoutBounds() {
        return new ChildBounds<>(this);
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
    public TimeLayoutBase<C, T> setChildStartTimeReader(Function<C, T> startTimeReader, boolean exclusive) {
        if (startTimeReader != this.childStartTimeReader || exclusive != this.childStartTimeExclusive) {
            this.childStartTimeReader = startTimeReader;
            childStartTimeExclusive = exclusive;
            invalidateTimesReading();
        }
        return this;
    }

    @Override
    public TimeLayoutBase<C, T> setChildEndTimeReader(Function<C, T> childEndTimeReader, boolean exclusive) {
        if (childEndTimeReader != this.childEndTimeReader || exclusive != this.childEndTimeExclusive) {
            this.childEndTimeReader = childEndTimeReader;
            childEndTimeExclusive = exclusive;
            invalidateTimesReading();
        }
        return this;
    }

    @Override
    public double getChildFixedHeight() {
        return childFixedHeight;
    }

    @Override
    public TimeLayoutBase<C, T> setChildFixedHeight(double childFixedHeight) {
        if (childFixedHeight != this.childFixedHeight) {
            this.childFixedHeight = childFixedHeight;
            invalidateVerticalLayout();
        }
        return this;
    }

    public boolean isChildFixedHeight() {
        return childFixedHeight >= 0;
    }

    @Override
    public boolean isFillHeight() {
        return fillHeight;
    }

    @Override
    public TimeLayoutBase<C, T> setFillHeight(boolean fillHeight) {
        if (fillHeight != this.fillHeight) {
            this.fillHeight = fillHeight;
            invalidateVerticalLayout();
        }
        return this;
    }

    @Override
    public double getTopY() {
        return topY;
    }

    @Override
    public TimeLayoutBase<C, T> setTopY(double topY) {
        if (topY != this.topY) {
            this.topY = topY;
            invalidateVerticalLayout();
        }
        return this;
    }

    @Override
    public double getHSpacing() {
        return hSpacing;
    }

    @Override
    public TimeLayoutBase<C, T> setHSpacing(double hSpacing) {
        if (hSpacing != this.hSpacing) {
            this.hSpacing = hSpacing;
            invalidateHorizontalLayout();
        }
        return this;
    }

    @Override
    public double getVSpacing() {
        return vSpacing;
    }

    @Override
    public TimeLayoutBase<C, T> setVSpacing(double vSpacing) {
        if (vSpacing != this.vSpacing) {
            this.vSpacing = vSpacing;
            invalidateVerticalLayout();
        }
        return this;
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
        if (isVisible()) {
            int newLayoutCount = getLayoutCount() + 1;
            layoutCountProperty.set(-newLayoutCount); // trigger onBeforeLayout runnable(s)
            if (!fillHeight && !heightProperty.isBound())
                setHeight(computeHeight());
            layoutDirtyMarker.markAsClean();
            layoutCountProperty.set(newLayoutCount); // trigger onAfterLayout runnable(s)
        }
    }

    public void invalidateTimesReading() {
        timeVersion++;
        invalidateHorizontalLayout(); // because x is a time projection (invalid times => invalid x)
    }

    public void invalidateHorizontalLayout() {
        horizontalVersion++;
        markLayoutAsDirty();
    }

    public void invalidateVerticalLayout() {
        verticalVersion++;
        rowsCount = -1;
        markLayoutAsDirty();
    }

    protected void readChildTimes(ChildBounds<C, T> cb) {
        C child = cb.getObject();
        cb.setStartTime(readChildStartTime(child));
        cb.setEndTime(readChildEndTime(child));
    }

    protected void layoutChildHorizontally(ChildBounds<C, T> cb) {
        TimeProjector<T> timeProjector = getTimeProjector();
        double startX = timeProjector.timeToX(cb.getStartTime(), true, childStartTimeExclusive);
        double endX = timeProjector.timeToX(cb.getEndTime(), false, childEndTimeExclusive);
        cb.setX(startX + hSpacing / 2);
        cb.setWidth(endX - startX - hSpacing);
    }

    protected void computeChildColumnIndex(ChildBounds<C, T> cb) {
        cb.setColumnIndex(0);
    }

    protected void layoutChildVertically(ChildBounds<C, T> cb) {
        if (fillHeight) {
            if (getRowsCount() > 0) {
                double rowHeight = (getHeight() - topY) / rowsCount;
                cb.setY(topY + cb.getRowIndex() * rowHeight);
                cb.setHeight(rowHeight);
            } else {
                cb.setY(topY);
                cb.setHeight(0);
            }
        } else {
            if (childFixedHeight > 0) {
                cb.setY(topY + (childFixedHeight + vSpacing) * cb.getRowIndex());
                cb.setHeight(childFixedHeight);
            }
        }
    }

    protected void computeChildRowIndex(ChildBounds<C, T> cb) {
        cb.setRowIndex(0);
    }

    protected double computeHeight() {
        if (childrenBounds.isEmpty())
            return 0;
        double maxMaxY = 0;
        for (ChildBounds<C, T> cb : childrenBounds) {
            double maxY = cb.getMaxY();
            maxMaxY = Math.max(maxMaxY, maxY);
        }
        return maxMaxY;
    }

    @Override
    public int getRowsCount() {
        if (rowsCount == -1) {
            rowsCount = 0;
            for (ChildBounds<C, T> cb : childrenBounds) {
                int rowIndex = cb.getRowIndex();
                rowsCount = Math.max(rowsCount, rowIndex + 1);
            }
        }
        return rowsCount;
    }

    @Override
    public ChildBounds<C, T> getChildBounds(int childIndex) {
        return childrenBounds.get(childIndex);
    }

    private T readChildStartTime(C child) {
        return childStartTimeReader.apply(child);
    }

    private T readChildEndTime(C child) {
        return childEndTimeReader.apply(child);
    }

    @Override
    public TimeProjector<T> getTimeProjector() {
        return timeProjector;
    }

    public TimeLayoutBase<C, T> setTimeProjector(TimeProjector<T> timeProjector) {
        this.timeProjector = timeProjector;
        return this;
    }

    @Override
    public boolean isSelectionEnabled() {
        return childSelectionEnabled;
    }

    @Override
    public TimeLayoutBase<C, T> setSelectionEnabled(boolean selectionEnabled) {
        this.childSelectionEnabled = selectionEnabled;
        return this;
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
        if (onlyIfSelectable && !isSelectionEnabled() || childrenBounds == null)
            return null;
        for (ChildBounds<C, T> cb : childrenBounds) {
            if (cb.contains(x, y))
                return cb.getObject();
        }
        return null;
    }

    @Override
    public void processVisibleChildren(javafx.geometry.Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<C, Bounds> childProcessor) {
        if (!isVisible() || visibleArea.getWidth() == 0 || visibleArea.getHeight() == 0)
            return;
        layoutIfDirty(); // ensuring the layout is done
        processVisibleChildrenNow(visibleArea, layoutOriginX, layoutOriginY, childProcessor);
    }

    protected void processVisibleChildrenNow(javafx.geometry.Bounds visibleArea, double layoutOriginX, double layoutOriginY, BiConsumer<C, Bounds> childProcessor) {
        TimeLayoutUtil.processVisibleObjectBounds(childrenBounds, false, visibleArea, layoutOriginX, layoutOriginY, childProcessor);
    }
}
