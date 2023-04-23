package dev.webfx.extras.timelayout;

import javafx.beans.property.ObjectProperty;

public interface CanSelectChild<C> {

    void setSelectedChild(C child);

    C getSelectedChild();

    ObjectProperty<C> selectedChildProperty();

    C pickChildAt(double x, double y);

    default C selectChildAt(double x, double y) {
        C child = pickChildAt(x, y);
        if (child != null)
            setSelectedChild(child);
        return child;
    }

}
