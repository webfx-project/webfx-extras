package dev.webfx.extras.cell.renderer;

import dev.webfx.extras.util.control.Controls;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Region;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bruno Salmon
 */
public final class ValueRendererRegistry {

    private static final Map<String, ValueRenderer> REGISTERED_VALUE_RENDERERS = new HashMap<>();

    public static void registerValueRenderer(String name, ValueRenderer valueRenderer) {
        REGISTERED_VALUE_RENDERERS.put(name, valueRenderer);
    }

    public static ValueRenderer getValueRenderer(String name) {
        return REGISTERED_VALUE_RENDERERS.get(name);
    }

    static {
        // Registering default generic renderers
        registerValueRenderer("boolean", BooleanRenderer.SINGLETON);
        registerValueRenderer("text", TextRenderer.SINGLETON);
        registerValueRenderer("image", ImageRenderer.SINGLETON);
        registerValueRenderer("imageText", ImageTextRenderer.SINGLETON);
        registerValueRenderer("label", (value, context) -> renderLabel(value, false, false));
        registerValueRenderer("wrappedLabel", (value, context) -> renderLabel(value, true, false));
        registerValueRenderer("ellipsisLabel", (value, context) -> renderLabel(value, true, true));
        registerValueRenderer("html", HtmlTextRenderer.SINGLETON);
    }

    public static Label renderLabel(Object value, boolean autoWrap, boolean ellipsis) {
        return renderLabeled(new Label(), value, autoWrap, ellipsis);
    }

    public static <L extends Labeled> L renderLabeled(L labeled, Object value, boolean autoWrap, boolean ellipsis) {
        ValueApplier.applyTextValue(value, labeled.textProperty());
        return Controls.setupTextWrapping(labeled, autoWrap, ellipsis);
    }

    public static void removePossibleLabelAutoWrap(Node node) {
        if (node instanceof Label label) {
            removePossibleLabelAutoWrap(label);
        }
    }

    public static void removePossibleLabelAutoWrap(Label label) {
        if (label.isWrapText() && label.getMinHeight() == Region.USE_PREF_SIZE) {
            label.setMinHeight(0);
        }
    }
}
