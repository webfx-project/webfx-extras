package dev.webfx.extras.visual;

import dev.webfx.extras.visual.impl.VisualStyleImpl;

/**
 * @author Bruno Salmon
 */
public final class VisualStyleBuilder {

    private Double minWidth;
    private Double prefWidth;
    private String textAlign;

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

    public VisualStyleBuilder setTextAlign(String textAlign) {
        this.textAlign = textAlign;
        return this;
    }

    public VisualStyle build() {
        return new VisualStyleImpl(minWidth, prefWidth, textAlign);
    }

    public static VisualStyleBuilder create() {
        return new VisualStyleBuilder();
    }
}
