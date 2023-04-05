package dev.webfx.extras.timelayout;

import javafx.collections.ObservableList;

import java.util.List;

public interface TimeLayout<C, T> extends TimeWindow<T>, CanLayout {

    // Input methods

    ObservableList<C> getChildren();

    void setChildTimeReader(ChildTimeReader<C, T> childTimeReader);

     double getChildFixedHeight();

    void setChildFixedHeight(double childFixedHeight);

    boolean isFillHeight();

    void setFillHeight(boolean fillHeight);

    double getTopY();

    void setTopY(double topY);

    double getHSpacing();

    void setHSpacing(double hSpacing);

    double getVSpacing();

    void setVSpacing(double vSpacing);


    // Output methods

    ChildPosition<T> getChildPosition(int childIndex);

    List<TimeColumn<T>> getColumns();

    List<TimeRow> getRows();

    int getRowsCount();

    }
