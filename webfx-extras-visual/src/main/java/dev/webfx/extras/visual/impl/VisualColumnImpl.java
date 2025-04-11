package dev.webfx.extras.visual.impl;

import dev.webfx.extras.cell.renderer.ValueRenderer;
import dev.webfx.extras.cell.renderer.ValueRenderingContext;
import dev.webfx.extras.label.Label;
import dev.webfx.extras.type.Type;
import dev.webfx.extras.visual.ColumnWidthCumulator;
import dev.webfx.extras.visual.VisualColumn;
import dev.webfx.extras.visual.VisualStyle;

/**
 * @author Bruno Salmon
 */
public final class VisualColumnImpl implements VisualColumn {

    private final Object headerValue;
    private final Label label;
    private final Type type;
    private final String role;
    private final VisualStyle style;
    private final ValueRenderer valueRenderer;
    private final ValueRenderingContext valueRenderingContext;
    private final ColumnWidthCumulator cumulator;
    private final Object source;

    public VisualColumnImpl(Object label, Type type) {
        this(label, label, type, null, null, null, null, null);
    }

    public VisualColumnImpl(Object headerValue, Object label, Type type, String role, VisualStyle style, ValueRenderer valueRenderer, ColumnWidthCumulator cumulator, Object source) {
        this.headerValue = headerValue;
        this.label = Label.from(label);
        this.type = type;
        this.role = role;
        this.style = style != null ? style : VisualStyle.NO_STYLE;
        this.valueRenderer = valueRenderer != null ? valueRenderer : ValueRenderer.create(type); // If not specified, we use a generic value renderer
        valueRenderingContext = this.label == null ? ValueRenderingContext.DEFAULT_READONLY_CONTEXT : new ValueRenderingContext(true, this.label, null, this.style.getTextAlign());
        this.cumulator = cumulator;
        this.source = source;
    }

    @Override
    public Object getHeaderValue() {
        return headerValue;
    }

    @Override
    public Label getLabel() {
        return label;
    }

    @Override
    public String getName() {
        return label.getText();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public VisualStyle getStyle() {
        return style;
    }

    @Override
    public ValueRenderer getValueRenderer() {
        return valueRenderer;
    }

    @Override
    public ValueRenderingContext getValueRenderingContext() {
        return valueRenderingContext;
    }

    @Override
    public ColumnWidthCumulator getCumulator() {
        return cumulator;
    }

    @Override
    public Object getSource() {
        return source;
    }

}
