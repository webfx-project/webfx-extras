package dev.webfx.extras.timelayout;

import javafx.collections.ObservableList;

import java.util.List;

public interface TimeLayout<T, C> {

    // Input methods

    ObservableList<C> getChildren();

    void setChildTimeReader(ChildTimeReader<T, C> childTimeReader);

    void setTimeWindow(T timeWindowStart, T timeWindowEnd);

    void setTimeColumnScale(TimeScale timeColumnScale);

    double getChildFixedHeight();

    void setChildFixedHeight(double childFixedHeight);

    boolean isFillHeight();

    void setFillHeight(boolean fillHeight);

    // layout method

    void layout(double width, double height);


    // Output methods

    ChildPosition<T> getChildPosition(int childIndex);

    List<TimeColumn<T>> getColumns();

    List<TimeRow> getRows();

    int getRowsCount();

    }
