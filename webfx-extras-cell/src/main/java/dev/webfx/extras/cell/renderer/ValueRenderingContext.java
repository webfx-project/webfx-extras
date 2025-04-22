package dev.webfx.extras.cell.renderer;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import dev.webfx.kit.util.properties.conversion.ConvertedProperty;
import dev.webfx.platform.util.function.Converter;

/**
 * @author Bruno Salmon
 */
public class ValueRenderingContext {

    public final static ValueRenderingContext DEFAULT_READONLY_CONTEXT = new ValueRenderingContext(true, null, null, null);

    private final boolean readOnly;
    // TODO: add immutable flag to skip binding
    private final Object labelKey;
    private final Object placeholderKey;
    private final String textAlign;
    private Property editedValueProperty;
    private int rowIndex;

    public ValueRenderingContext(boolean readOnly, Object labelKey, Object placeholderKey, String textAlign) {
        this.readOnly = readOnly;
        this.labelKey = labelKey;
        this.placeholderKey = placeholderKey;
        this.textAlign = textAlign;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public Object getLabelKey() {
        return labelKey;
    }

    public Object getPlaceholderKey() {
        return placeholderKey;
    }

    public String getTextAlign() {
        return textAlign;
    }

    public Property getEditedValueProperty() {
        if (editedValueProperty == null)
            editedValueProperty = new SimpleObjectProperty<>();
        return editedValueProperty;
    }

    public void setEditedValue(Object value) {
        if (value != null || editedValueProperty != null)
            getEditedValueProperty().setValue(value);
    }

    public Object getEditedValue() {
        return editedValueProperty == null ? null : editedValueProperty.getValue();
    }

    public <T> void bindEditedValuePropertyTo(Property<T> nodeProperty, T initialValue) {
        bindEditedValuePropertyTo(editedValueProperty, nodeProperty, initialValue);
    }

    public <A, B> void bindConvertedEditedValuePropertyTo(Property<A> nodeProperty, A initialValue, Converter<A, B> aToBConverter, Converter<B, A> bToAConverter) {
        bindEditedValuePropertyTo(new ConvertedProperty<A, B>(getEditedValueProperty(), aToBConverter, bToAConverter), nodeProperty, initialValue);
    }

    private <T> void bindEditedValuePropertyTo(Property<T> contextProperty, Property<T> nodeProperty, T initialValue) {
        // QUICK TEMPORARY FIX START for KBSX demo (without this fix, all booleans have the same value in a table column - ex: Authorization activity)
        if (isReadOnly())
            contextProperty = new SimpleObjectProperty<>();
        // QUIT TEMPORARY FIX END
        if (contextProperty == null)
            contextProperty = editedValueProperty = nodeProperty;
        contextProperty.setValue(initialValue);
        if (isReadOnly()) {
            if (contextProperty != nodeProperty)
                nodeProperty.bind(contextProperty);
        } else if (contextProperty != nodeProperty)
            nodeProperty.bindBidirectional(contextProperty);
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }
}
