package dev.webfx.extras.visual;

import dev.webfx.extras.visual.impl.VisualColumnImpl;
import dev.webfx.extras.type.Type;
import dev.webfx.extras.cell.renderer.ValueRenderer;

/**
 * @author Bruno Salmon
 */
public final class VisualColumnBuilder {

    private Object headerValue;
    private Object label;
    private Type type;
    private String role;
    private VisualStyle style;
    private ValueRenderer valueRenderer;
    private ColumnWidthAccumulator accumulator;
    private Object source;

    public VisualColumnBuilder(Object label, Type type) {
        this.label = headerValue = label;
        this.type = type;
    }

    public VisualColumnBuilder setHeaderValue(Object headerValue) {
        this.headerValue = headerValue;
        return this;
    }

    public VisualColumnBuilder setLabel(Object label) {
        this.label = label;
        return this;
    }

    public VisualColumnBuilder setType(Type type) {
        this.type = type;
        return this;
    }

    public VisualColumnBuilder setRole(String role) {
        this.role = role;
        return this;
    }

    public VisualColumnBuilder setStyle(VisualStyle style) {
        this.style = style;
        return this;
    }

    public VisualColumnBuilder setValueRenderer(ValueRenderer valueRenderer) {
        this.valueRenderer = valueRenderer;
        return this;
    }

    public VisualColumnBuilder setAccumulator(ColumnWidthAccumulator accumulator) {
        this.accumulator = accumulator;
        return this;
    }

    public VisualColumnBuilder setSource(Object source) {
        this.source = source;
        return this;
    }

    public VisualColumn build() {
        return new VisualColumnImpl(headerValue, label, type, role, style, valueRenderer, accumulator, source);
    }

    public static VisualColumnBuilder create() {
        return create(null);
    }

    public static VisualColumnBuilder create(Object label) {
        return create(label, null);
    }

    public static VisualColumnBuilder create(Object label, Type type) {
        return new VisualColumnBuilder(label, type);
    }
}
