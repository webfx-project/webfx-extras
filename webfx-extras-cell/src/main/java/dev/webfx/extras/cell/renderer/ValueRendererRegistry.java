package dev.webfx.extras.cell.renderer;

import javafx.scene.Node;
import javafx.scene.control.Label;
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
        Label label = new Label();
        if (autoWrap) {
            label.setWrapText(true);
            // Note that setting wrapText = true is not enough to make the label automatically wrap the text, the other
            // condition being to constraint either 1) the width or 2) the height of the label as follows:
            // 1) label.prefWidthProperty().bind(vBox.widthProperty());
            // 2) label.setMinHeight(Region.USE_PREF_SIZE);
            // We choose 2) as it doesn't need a reference to the container. In addition, it's not sure that the label
            // needs to have the exact same width as the container (it may have margin or be laid out differently).
            // So 2) is definitely more generic.
            label.setMinHeight(Region.USE_PREF_SIZE);
            // This is the general default behavior for "wrappedLabel" and "ellipsisLabel", but some renderers may
            // wish a different behavior. In that case, they can call removePossibleLabelAutoWrap() method (see for
            // example VisualGridTableSkin.setCellContent() method).
        }
        if (ellipsis)
            label.getStyleClass().add("ellipsis");
        ValueApplier.applyValue(value, label.textProperty());
        return label;
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
