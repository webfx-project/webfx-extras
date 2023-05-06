package dev.webfx.extras.layer.interact;

import javafx.beans.property.ObjectProperty;

/**
 * @author Bruno Salmon
 */
public interface CanSelectChild<T> {

    void setSelectionEnabled(boolean selectionEnabled);

    boolean isSelectionEnabled();

    ObjectProperty<T> selectedChildProperty();

    default void setSelectedChild(T child) {
        selectedChildProperty().set(child);
    }

    default T getSelectedChild() {
        return selectedChildProperty().get();
    }

    T pickChildAt(double x, double y, boolean onlyIfSelectable);

    default T selectChildAt(double x, double y) {
        if (!isSelectionEnabled())
            return null;
        T child = pickChildAt(x, y, true);
        if (child != null)
            setSelectedChild(child);
        return child;
    }

}
