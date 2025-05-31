package dev.webfx.extras.aria;

import dev.webfx.kit.util.aria.Aria;
import dev.webfx.kit.util.aria.AriaRole;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.util.collection.Collections;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Bruno Salmon
 */
public final class AriaToggleGroup<T> {

    private static final String ARIA_TOGGLE_STYLE_CLASS = "aria-toggle";
    private static final String FIRED_STYLE_CLASS = "fired";
    private static int GROUP_SEQ;
    private static int BUTTON_SEQ;

    private final AriaRole buttonRole;
    private final ToggleGroup focusGroup = new ToggleGroup();
    private final ObjectProperty<T> firedItemProperty = FXProperties.newObjectProperty(this::onFiredItemChanged);
    private final HashMap<T, ToggleButton> itemButtonMap = new HashMap<>();
    private final int groupId = ++GROUP_SEQ;

    public AriaToggleGroup() {
        this(null);
    }

    public AriaToggleGroup(AriaRole buttonRole) {
        this.buttonRole = buttonRole;
        // Buttons behave like standard toggle buttons, except that we can't unselect them by clicking on the same
        // button again. So if the user does that, we undo that deselection and reestablish the selection.
        FXProperties.runOnPropertyChange(selectedToggle -> {
            //Console.log("selectedToggle changed to " + selectedToggle + " for group " + groupId);
            if (selectedToggle == null) // indicates that the user clicked on the selected date button again
                updateButtonsFiredStyleClass(); // we reestablish the button selection from the selected item
        }, focusGroup.selectedToggleProperty());
        //Console.log("Creating group " + groupId);
    }

    public void clear() {
        //Console.log("clear group " + groupId);
        focusGroup.getToggles().clear();
        itemButtonMap.clear();
        setFiredItem(null);
    }

    public ToggleButton createItemButton(T item) {
        return registerItemButton(new ToggleButton(), item, true);
    }

    public ToggleButton registerItemButton(ToggleButton toggleButton, T item, boolean callSetFiredItemOnAction) {
        toggleButton.setToggleGroup(focusGroup);
        if (callSetFiredItemOnAction) {
            EventHandler<ActionEvent> oldAction = toggleButton.getOnAction();
            toggleButton.setOnAction(e -> {
                setFiredItem(item);
                if (oldAction != null)
                    oldAction.handle(e);
            });
        }
        Collections.addIfNotContains(ARIA_TOGGLE_STYLE_CLASS, toggleButton.getStyleClass());
        Aria.setAriaRole(toggleButton, buttonRole);
        itemButtonMap.put(item, toggleButton);
        toggleButton.setId("" + BUTTON_SEQ++);
/*
        toggleButton.selectedProperty().addListener((obs, oldSelected, newSelected) -> {
            Console.log("toggleButton " + toggleButton + " selected changed to " + newSelected + " in group " + groupId);
        });
        Console.log("Registered toggle button " + toggleButton + " in group " + groupId);
*/
        return toggleButton;
    }

    public void setFiredItem(T item) {
        firedItemProperty.setValue(item);
    }

    public T getFiredItem() {
        return firedItemProperty.getValue();
    }

    public ObjectProperty<T> firedItemProperty() {
        return firedItemProperty;
    }

    private void onFiredItemChanged(T newValue) {
        //Console.log("👉👉👉👉👉 onFiredItemChanged -> " + newValue + " for group " + groupId);
        updateButtonsFiredStyleClass();
    }

    public void updateButtonsFiredStyleClass() {
        T firedItem = getFiredItem();
        // Marking the selected date button with the CSS class "fired"
        for (Map.Entry<T, ToggleButton> entry : itemButtonMap.entrySet()) {
            ToggleButton toggleButton = entry.getValue();
            T buttonItem = entry.getKey();
            boolean isFiredButton = Objects.equals(firedItem, buttonItem);
            Collections.addIfNotContainsOrRemove(toggleButton.getStyleClass(), isFiredButton, FIRED_STYLE_CLASS);
            if (isFiredButton) {
                //Console.log("Setting selected toggle button " + toggleButton + " for group " + groupId);
                focusGroup.selectToggle(toggleButton);
            }
        }
    }
}
