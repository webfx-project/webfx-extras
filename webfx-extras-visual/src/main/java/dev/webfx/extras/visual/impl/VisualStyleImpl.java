package dev.webfx.extras.visual.impl;

import dev.webfx.extras.visual.VisualStyle;

/**
 * @author Bruno Salmon
 */
public final class VisualStyleImpl implements VisualStyle {

    private final Double minWidth;
    private final Double prefWidth;
    private final Double maxWidth;
    private final Boolean hGrow;
    private final Boolean hShrink;
    private final String textAlign;

    public VisualStyleImpl() {
        this(null, null, null, null, null, null);
    }

    public VisualStyleImpl(Double minWidth, Double prefWidth, Double maxWidth, Boolean hGrow, Boolean hShrink, String textAlign) {
        this.minWidth = minWidth;
        this.prefWidth = prefWidth;
        this.maxWidth = maxWidth;
        this.hGrow = hGrow;
        this.hShrink = hShrink;
        this.textAlign = textAlign;
    }

    @Override
    public Double getMinWidth() {
        return minWidth;
    }

    public Double getPrefWidth() {
        return prefWidth;
    }

    @Override
    public Double getMaxWidth() {
        return maxWidth;
    }

    @Override
    public Boolean getHGrow() {
        return hGrow;
    }

    @Override
    public Boolean getHShrink() {
        return hShrink;
    }

    public String getTextAlign() {
        return textAlign;
    }
}
