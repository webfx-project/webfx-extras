package dev.webfx.extras.styles.materialdesign.textfield;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

/**
 * @author Bruno Salmon
 */
public interface MaterialTextFieldMixin extends MaterialTextField {

    MaterialTextField getMaterialTextField();

    @Override
    default ObservableValue inputProperty() {
        return getMaterialTextField().inputProperty();
    }

    @Override
    default boolean isInputEmpty() {
        return getMaterialTextField().isInputEmpty();
    }

    default StringProperty placeholderTextProperty() {
        return getMaterialTextField().placeholderTextProperty();
    }

    default String getPlaceholderText() {
        return getMaterialTextField().getPlaceholderText();
    }

    default void setPlaceholderText(String text) {
        getMaterialTextField().setPlaceholderText(text);
    }

    default StringProperty labelTextProperty() {
        return getMaterialTextField().labelTextProperty();
    }

    default String getLabelText() {
        return getMaterialTextField().getLabelText();
    }

    default void setLabelText(String text) {
        getMaterialTextField().setLabelText(text);
    }

    default StringProperty helperTextProperty() {
        return getMaterialTextField().helperTextProperty();
    }

    default String getHelperText() {
        return getMaterialTextField().getHelperText();
    }

    default void setHelperText(String text) {
        getMaterialTextField().setHelperText(text);
    }

    default StringProperty errorMessageProperty() {
        return getMaterialTextField().errorMessageProperty();
    }

    default String getErrorMessage() {
        return getMaterialTextField().getErrorMessage();
    }

    default void setErrorMessage(String text) {
        getMaterialTextField().setErrorMessage(text);
    }

    default ReadOnlyBooleanProperty disabledProperty() {
        return getMaterialTextField().disabledProperty();
    }

    default boolean isDisabled() {
        return getMaterialTextField().isDisabled();
    }

    default void setDisable(boolean disable) {
        getMaterialTextField().setDisable(disable);
    }

    default BooleanProperty requiredProperty() {
        return getMaterialTextField().requiredProperty();
    }

    default boolean isRequired() {
        return getMaterialTextField().isRequired();
    }

    default void setRequired(boolean required) {
        getMaterialTextField().setRequired(required);
    }

    default ReadOnlyBooleanProperty focusedProperty() {
        return getMaterialTextField().focusedProperty();
    }

    default boolean isFocused() {
        return getMaterialTextField().isFocused();
    }

    default BooleanProperty denseSpacingProperty() {
        return getMaterialTextField().denseSpacingProperty();
    }

    default boolean isDenseSpacing() {
        return getMaterialTextField().isDenseSpacing();
    }

    default void setDenseSpacing(boolean denseSpacing) {
        getMaterialTextField().setDenseSpacing(denseSpacing);
    }

}
