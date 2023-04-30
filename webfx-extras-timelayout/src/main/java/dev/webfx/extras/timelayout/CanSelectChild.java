package dev.webfx.extras.timelayout;

import javafx.beans.property.ObjectProperty;

public interface CanSelectChild<C> {

    void setChildSelectionEnabled(boolean childSelectionEnabled);

    boolean isChildSelectionEnabled();

    ObjectProperty<C> selectedChildProperty();

    default void setSelectedChild(C child) {
        selectedChildProperty().set(child);
    }

    default C getSelectedChild() {
        return selectedChildProperty().get();
    }

    C pickChildAt(double x, double y, boolean onlyIfSelectable);

    default C selectChildAt(double x, double y) {
        if (!isChildSelectionEnabled())
            return null;
        C child = pickChildAt(x, y, true);
        if (child != null)
            setSelectedChild(child);
        return child;
    }

}
