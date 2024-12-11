package dev.webfx.extras.styles.materialdesign.textfield;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

/**
 * @author Bruno Salmon
 */
public interface MaterialTextField {

    ObservableValue inputProperty();

    default boolean isInputEmpty() {
        ObservableValue observableValue = inputProperty();
        if (inputProperty() == null)
            return false;
        Object input = observableValue.getValue();
        return input == null || input instanceof String && ((String) input).isEmpty();
    }

    StringProperty placeholderTextProperty();

    default String getPlaceholderText() {
        return placeholderTextProperty().getValue();
    }

    default void setPlaceholderText(String text) {
        placeholderTextProperty().setValue(text);
    }

    StringProperty labelTextProperty();

    default String getLabelText() {
        return labelTextProperty().getValue();
    }

    default void setLabelText(String text) {
        labelTextProperty().setValue(text);
    }

    StringProperty helperTextProperty();

    default String getHelperText() {
        return helperTextProperty().getValue();
    }

    default void setHelperText(String text) {
        helperTextProperty().setValue(text);
    }

    StringProperty errorMessageProperty();

    default String getErrorMessage() {
        return errorMessageProperty().getValue();
    }

    default void setErrorMessage(String text) {
        errorMessageProperty().setValue(text);
    }

    ReadOnlyBooleanProperty disabledProperty();

    default boolean isDisabled() {
        return disabledProperty().getValue();
    }

    default void setDisable(boolean disabled) {
        ((BooleanProperty) disabledProperty()).set(disabled);
    }

    BooleanProperty requiredProperty();

    default boolean isRequired() {
        return requiredProperty().getValue();
    }

    default void setRequired(boolean required) {
        requiredProperty().set(required);
    }

    ReadOnlyBooleanProperty focusedProperty();

    default boolean isFocused() {
        return focusedProperty().getValue();
    }

    BooleanProperty denseSpacingProperty();

    default boolean isDenseSpacing() {
        return denseSpacingProperty().getValue();
    }

    default void setDenseSpacing(boolean denseSpacing) {
        denseSpacingProperty().set(denseSpacing);
    }

}
