package dev.webfx.extras.visual.impl;

import dev.webfx.extras.visual.VisualStyle;

/**
 * @author Bruno Salmon
 */
public final class VisualStyleImpl implements VisualStyle {

    private final Double minWidth;
    private final Double prefWidth;
    private final String textAlign;

    public VisualStyleImpl() {
        this(null, null, null);
    }

    public VisualStyleImpl(Double minWidth, Double prefWidth, String textAlign) {
        this.minWidth = minWidth;
        this.prefWidth = prefWidth;
        this.textAlign = textAlign;
    }

    @Override
    public Double getMinWidth() {
        return minWidth;
    }

    public Double getPrefWidth() {
        return prefWidth;
    }

    public String getTextAlign() {
        return textAlign;
    }
}
