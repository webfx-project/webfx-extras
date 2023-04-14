package dev.webfx.extras.timelayout;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

import java.util.List;

public interface TimeLayout<C, T> extends TimeWindow<T>, CanLayout {

    // Input methods

    ObservableList<C> getChildren();

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

    default void setInclusiveChildStartTimeReader(ChildTimeReader<C, T> startTimeReader) {
        setChildStartTimeReader(startTimeReader, false);
    }

    default void setExclusiveChildStartTimeReader(ChildTimeReader<C, T> startTimeReader) {
        setChildStartTimeReader(startTimeReader, true);
    }

    void setChildStartTimeReader(ChildTimeReader<C, T> startTimeReader, boolean exclusive);

    default void setInclusiveChildEndTimeReader(ChildTimeReader<C, T> endTimeReader) {
        setChildEndTimeReader(endTimeReader, false);
    }

    default void setExclusiveChildEndTimeReader(ChildTimeReader<C, T> endTimeReader) {
        setChildEndTimeReader(endTimeReader, true);
    }

    void setChildEndTimeReader(ChildTimeReader<C, T> childEndTimeReader, boolean exclusive);


    // Output methods

    ChildPosition<T> getChildPosition(int childIndex);

    List<TimeColumn<T>> getColumns();

    List<TimeRow> getRows();

    int getRowsCount();

    void setSelectedChild(C child);

    C getSelectedChild();

    ObjectProperty<C> selectedChildProperty();

    C pickChild(double x, double y);

    default boolean selectClickedChild(double x, double y) {
        C child = pickChild(x, y);
        if (child != null)
            setSelectedChild(child);
        return child != null;
    }

    boolean isVisible();

    void setVisible(boolean visible);

}
