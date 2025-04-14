package dev.webfx.extras.visual;

import dev.webfx.extras.visual.impl.VisualStyleImpl;

/**
 * @author Bruno Salmon
 */
public interface VisualStyle {

    Double getMinWidth();

    Double getPrefWidth();

    String getTextAlign();

    VisualStyle NO_STYLE = new VisualStyleImpl();
    VisualStyle CENTER_STYLE = new VisualStyleImpl(null, null, "center");
    VisualStyle RIGHT_STYLE = new VisualStyleImpl(null, null, "right");

}
