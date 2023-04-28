package dev.webfx.extras.timelayout;

import javafx.beans.property.ObjectProperty;

public interface CanSelectChild<C> {

    ObjectProperty<C> selectedChildProperty();

    default void setSelectedChild(C child) {
        selectedChildProperty().set(child);
    }

    default C getSelectedChild() {
        return selectedChildProperty().get();
    }

    C pickChildAt(double x, double y);

    default C selectChildAt(double x, double y) {
        C child = pickChildAt(x, y);
        if (child != null)
            setSelectedChild(child);
        return child;
    }

}
