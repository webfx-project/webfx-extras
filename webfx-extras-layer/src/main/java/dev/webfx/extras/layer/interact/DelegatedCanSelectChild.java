package dev.webfx.extras.layer.interact;

import javafx.beans.property.ObjectProperty;

/**
 * @author Bruno Salmon
 */
public class DelegatedCanSelectChild<T> implements CanSelectChild<T> {

    private final CanSelectChild<T> delegate;

    public DelegatedCanSelectChild(CanSelectChild<T> delegate) {
        this.delegate = delegate;
    }

    public CanSelectChild<T> getDelegate() {
        return delegate;
    }

    @Override
    public CanSelectChild<T> setSelectionEnabled(boolean selectionEnabled) {
        return getDelegate().setSelectionEnabled(selectionEnabled);
    }

    @Override
    public boolean isSelectionEnabled() {
        return getDelegate().isSelectionEnabled();
    }

    @Override
    public ObjectProperty<T> selectedChildProperty() {
        return getDelegate().selectedChildProperty();
    }

    @Override
    public void setSelectedChild(T child) {
        getDelegate().setSelectedChild(child);
    }

    @Override
    public T getSelectedChild() {
        return getDelegate().getSelectedChild();
    }

    @Override
    public T pickChildAt(double x, double y, boolean onlyIfSelectable) {
        return getDelegate().pickChildAt(x, y, onlyIfSelectable);
    }

    @Override
    public T selectChildAt(double x, double y) {
        return getDelegate().selectChildAt(x, y);
    }
}
