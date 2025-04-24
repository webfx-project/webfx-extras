package dev.webfx.extras.visual;

import dev.webfx.extras.visual.impl.VisualStyleImpl;

/**
 * @author Bruno Salmon
 */
public interface VisualStyle {

    Double getMinWidth();

    Double getPrefWidth();

    Double getMaxWidth();

    Boolean getHGrow();

    Boolean getHShrink();

    String getTextAlign();

    String getStyleClass();

    VisualStyle NO_STYLE = new VisualStyleImpl();
    VisualStyle CENTER_STYLE = new VisualStyleImpl(null, null, null, null, null, "center", null);
    VisualStyle RIGHT_STYLE = new VisualStyleImpl(null, null, null, null, null, "right", null);

}
