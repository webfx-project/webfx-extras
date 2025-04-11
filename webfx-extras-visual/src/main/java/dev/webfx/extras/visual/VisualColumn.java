package dev.webfx.extras.visual;

import dev.webfx.extras.visual.impl.VisualColumnImpl;
import dev.webfx.extras.type.Type;
import dev.webfx.extras.cell.renderer.ValueRenderer;
import dev.webfx.extras.cell.renderer.ValueRenderingContext;
import dev.webfx.extras.label.Label;

/**
 * @author Bruno Salmon
 */
public interface VisualColumn {

    /**
     * @return a header value that a column may hold.
     */
    Object getHeaderValue();

    /**
     * @return the label (icon and text) to use if the column header is displayed on the screen.
     */
    Label getLabel();

    /**
     * @return the name of the column (which is the text defined in label)
     */
    String getName();

    /**
     * @return The expected type for the column values (for all rows).
     */
    Type getType();

    /**
     * @return a specific role that this column is playing (ex: if the column contains the css class to apply to the row in a
     * table, or the series names in a chart using the column format, ...). Should be null if the column has the default
     * role for the visual component it is used for (the default role usually means the column contains domain data).
     */
    String getRole();

    /**
     * @return The style information to apply when rendering this column.
     */
    VisualStyle getStyle();

    /**
     * @return The value renderer to be used for this column to transform the cell values into graphical nodes.
     */
    ValueRenderer getValueRenderer();

    ValueRenderingContext getValueRenderingContext();

    ColumnWidthCumulator getCumulator();

    Object getSource(); // Ex: EntityColumn

    /**
     * Quick factory method for a simple VisualColumn creation with just a label and type. Use the VisualColumnBuilder
     * for more complex cases.
     */
    static VisualColumn create(Object label, Type type) {
        return new VisualColumnImpl(label, type);
    }

    static VisualColumn create(Object label, Type type, VisualStyle style) {
        return new VisualColumnImpl(label, label, type, null, style, null, null, null);
    }

    static VisualColumn create(ValueRenderer valueRenderer) {
        return new VisualColumnImpl(null, null, null, null, null, valueRenderer, null, null);
    }
}
