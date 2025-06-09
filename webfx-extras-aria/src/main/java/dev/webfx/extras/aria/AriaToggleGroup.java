package dev.webfx.extras.aria;

import dev.webfx.kit.util.aria.Aria;
import dev.webfx.kit.util.aria.AriaRole;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.ObservableLists;
import dev.webfx.platform.util.collection.Collections;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Objects;

/**
 * This class helps to build a group of accessible buttons. It doesn't provide the container (the app code is responsible
 * for inserting them in the UI), but it helps to manage them. It imposes the usage of toggle buttons (either created by
 * this class or passed by the app code). This is because the keyboard navigation is already implemented out of the box
 * for them in JavaFX. The selected toggle represents the button that has currently the focus in that keyboard navigation
 * (and not the button that has been fired). The focused button can be styled using the CSS variables provided in this
 * module, or directly using the `selected` pseudo class (i.e., `:selected` in JavaFX CSS and `.pseudo-selected` in web
 * CSS).
 * The app may also want to highlight the fired button (like in the Modality frontoffice navigation bar that makes the
 * button bold when the user is visiting its page). To achieve that, the app code must associate each button with an
 * item of type T and firedItemProperty will hold the fired item (in the example mentioned above, it will be the current
 * route). This class will automatically set the fired item when its associated button is fired (this default behavior
 * can be disabled), but the app code can also set or bind it (ex: to the browser history location, so that when the
 * user navigates backward or forward, this reevaluates which button is in the fired state. This class will automatically
 * add the `fired` style class to the fired button, which can be styled using the CSS variables.
 * Last detail: normally toggle buttons can be deselected when the user clicks again on the selected one, but this is
 * not wished here (as selected means focused here), so this class prevents this default behavior.
 *
 * @author Bruno Salmon
 */
public final class AriaToggleGroup<T> {

    private static final String ARIA_TOGGLE_STYLE_CLASS = "aria-toggle";
    private static final String FIRED_STYLE_CLASS = "fired";
    private static final String FIRST_CHILD_STYLE_CLASS = "first-child";
    private static final String LAST_CHILD_STYLE_CLASS = "last-child";
    private static final Object ITEM_PROPERTY_KEY = "webfx-item";
    private static int GROUP_SEQ;
    private static int BUTTON_SEQ;

    private final AriaRole buttonRole;
    private final ToggleGroup focusGroup = new ToggleGroup();
    private final ObservableList<ToggleButton> toggleButtons = ObservableLists.map(focusGroup.getToggles(), toggle -> (ToggleButton) toggle);
    private final ObjectProperty<T> firedItemProperty = FXProperties.newObjectProperty(this::onFiredItemChanged);
    private final int groupId = ++GROUP_SEQ;

    public AriaToggleGroup() {
        this(null);
    }

    public AriaToggleGroup(AriaRole buttonRole) {
        this.buttonRole = buttonRole;
        // Buttons behave like standard toggle buttons, except that we can't unselect them by clicking on the same
        // button again. So if the user does that, we undo that deselection and reestablish the selection.
        FXProperties.runOnPropertyChange((o, oldToggle, newToggle) -> {
            if (newToggle == null) // indicates that the user clicked on the selected date button again
                focusGroup.selectToggle(oldToggle);
        }, focusGroup.selectedToggleProperty());
        //Console.log("Creating group " + groupId);
    }

    public void clear() {
        //Console.log("clear group " + groupId);
        focusGroup.getToggles().clear();
        setFiredItem(null);
    }

    public ToggleButton createItemButton(T item) {
        return registerItemButton(new ToggleButton(), item, true);
    }

    public ToggleButton registerItemButton(ToggleButton toggleButton, T item, boolean callSetFiredItemOnAction) {
        toggleButton.setToggleGroup(focusGroup);
        toggleButton.addEventFilter(KeyEvent.ANY, e ->
            FXKeyboardNavigationDetected.setKeyboardNavigationDetected(true)
        );
        toggleButton.addEventFilter(MouseEvent.MOUSE_CLICKED, e ->
            FXKeyboardNavigationDetected.setKeyboardNavigationDetected(false)
        );
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
        toggleButton.getProperties().put(ITEM_PROPERTY_KEY, item);
        toggleButton.setId("" + BUTTON_SEQ++);
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

    public ObservableList<ToggleButton> getToggleButtons() {
        return toggleButtons;
    }

    public void updateButtonsFiredStyleClass() {
        T firedItem = getFiredItem();
        // Marking the selected date button with the CSS class "fired"
        ToggleButton firstButton = Collections.first(toggleButtons);
        ToggleButton lastButton = Collections.last(toggleButtons);
        toggleButtons.forEach(toggleButton -> {
            T buttonItem = (T) toggleButton.getProperties().get(ITEM_PROPERTY_KEY);
            ObservableList<String> styleClass = toggleButton.getStyleClass();
            Collections.addIfNotContainsOrRemove(styleClass, toggleButton == firstButton, FIRST_CHILD_STYLE_CLASS);
            Collections.addIfNotContainsOrRemove(styleClass, toggleButton == lastButton, LAST_CHILD_STYLE_CLASS);
            Collections.addIfNotContainsOrRemove(styleClass, Objects.equals(firedItem, buttonItem), FIRED_STYLE_CLASS);
            Scene scene = toggleButton.getScene();
            if (scene != null && scene.getFocusOwner() == toggleButton) {
                //Console.log("Setting selected toggle button " + toggleButton + " for group " + groupId);
                focusGroup.selectToggle(toggleButton);
            }
        });
    }
}
