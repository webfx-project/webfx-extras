package dev.webfx.extras.cell.renderer;

import dev.webfx.kit.util.properties.FXProperties;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

/**
 * @author Bruno Salmon
 */
public final class ValueApplier {

    /**
     * Applies the value to a graphical property that will render the value (typically a textProperty from Text, Label,
     * HtmlText, etc...). The value can be immutable, or an observable value, in which case the property will be bound
     * to it.
     *
     * @param value the value (immutable or observable value) whose type is compatible with the property
     * @param property the property to set or bind with the value
     */
    static void applyValue(Object value, Property property) {
        // If the value is observable (ex: from I18n or LocalizedDateTimeFormat), we bind the property so that it is
        // automatically updated on changes (ex: when the user changes the i18n language).
        if (value instanceof ObservableValue<?>) {
            property.bind((ObservableValue<?>) value);
        } else // Otherwise we just set the value (forcing unbinding if necessary)
            FXProperties.setEvenIfBound(property, value);
    }

    public static Object getApplicableValue(Object value) {
        return value instanceof ObservableValue<?> ? ((ObservableValue<?>) value).getValue() : value;
    }

}
