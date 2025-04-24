package dev.webfx.extras.visual;

import dev.webfx.extras.visual.impl.VisualStyleImpl;

/**
 * @author Bruno Salmon
 */
public final class VisualStyleBuilder {

    private Double minWidth;
    private Double prefWidth;
    private Double maxWidth;
    private Boolean hGrow;
    private Boolean hShrink;
    private String textAlign;
    private String styleClass;

    private VisualStyleBuilder() {
    }

    public VisualStyleBuilder setMinWidth(Double minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    public VisualStyleBuilder setPrefWidth(Double prefWidth) {
        this.prefWidth = prefWidth;
        return this;
    }

    public VisualStyleBuilder setMaxWidth(Double maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public VisualStyleBuilder setHGrow(Boolean hGrow) {
        this.hGrow = hGrow;
        return this;
    }

    public VisualStyleBuilder setHShrink(Boolean hShrink) {
        this.hShrink = hShrink;
        return this;
    }

    public VisualStyleBuilder setTextAlign(String textAlign) {
        this.textAlign = textAlign;
        return this;
    }

    public VisualStyleBuilder setStyleClass(String styleClass) {
        this.styleClass = styleClass;
        return this;
    }

    public VisualStyle build() {
        return new VisualStyleImpl(minWidth, prefWidth, maxWidth, hGrow, hShrink, textAlign, styleClass);
    }

    public static VisualStyleBuilder create() {
        return new VisualStyleBuilder();
    }
}
