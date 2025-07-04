package dev.webfx.extras.action.impl;

import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.util.collection.Collections;
import dev.webfx.extras.action.Action;
import dev.webfx.extras.action.ActionGroup;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public final class ActionGroupImpl extends ReadOnlyAction implements ActionGroup {

    private final Collection<Action> actions;
    private final ObservableList<Action> visibleActions = FXCollections.observableArrayList();
    private final boolean hasSeparators;

    public ActionGroupImpl(Collection<Action> actions, ObservableStringValue textProperty, ObservableValue<Supplier<Node>> graphicFactoryProperty, ObservableBooleanValue disabledProperty, ObservableBooleanValue visibleProperty, boolean hasSeparators, EventHandler<ActionEvent> actionHandler) {
        super(textProperty, graphicFactoryProperty, disabledProperty, visibleProperty, actionHandler);
        this.actions = actions;
        this.hasSeparators = hasSeparators;
        FXProperties.runNowAndOnPropertiesChange(this::updateVisibleActions, Collections.map(actions, Action::visibleProperty));
    }

    private void updateVisibleActions() {
        List<Action> actions = new ArrayList<>();
        boolean addSeparatorOnNextAdd = false;
        for (Action action : this.actions) {
            if (action.isVisible()) {
                int n = actions.size();
                if (action.getText() == null && action.getGraphicFactory() == null && action instanceof ActionGroup) {
                    ActionGroup actionGroup = (ActionGroup) action;
                    actions.addAll(actionGroup.getVisibleActions());
                    if (actions.size() > n) {
                        if (addSeparatorOnNextAdd || n > 0 && actionGroup.hasSeparators())
                            actions.add(n, new SeparatorAction());
                        addSeparatorOnNextAdd = actionGroup.hasSeparators();
                    }
                } else {
                    if (addSeparatorOnNextAdd)
                        actions.add(new SeparatorAction());
                    actions.add(action);
                    addSeparatorOnNextAdd = false;
                }
            }
        }
        this.visibleActions.setAll(actions);
        ObservableBooleanValue groupVisibleObservableValue = visibleProperty();
        if (groupVisibleObservableValue instanceof Property)
            FXProperties.setIfNotBound((Property) groupVisibleObservableValue, !this.visibleActions.isEmpty());
    }

    @Override
    public Collection<Action> getActions() {
        return actions;
    }

    @Override
    public ObservableList<Action> getVisibleActions() {
        return visibleActions;
    }

    @Override
    public boolean hasSeparators() {
        return hasSeparators;
    }
}
