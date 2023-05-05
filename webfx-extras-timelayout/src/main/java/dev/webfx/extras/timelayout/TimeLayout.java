package dev.webfx.extras.timelayout;

import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;

public interface TimeLayout<C, T> extends ListenableTimeWindow<T>, CanLayout, CanSelectChild<C> {

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

    BooleanProperty visibleProperty();

    default boolean isVisible() {
        return visibleProperty().get();
    }

    default void setVisible(boolean visible) {
        visibleProperty().set(visible);
    }

    // Output methods

    LayoutPosition getChildPosition(int childIndex);

    int getRowsCount();

}
