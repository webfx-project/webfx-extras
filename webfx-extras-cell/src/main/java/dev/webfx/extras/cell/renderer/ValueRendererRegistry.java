package dev.webfx.extras.cell.renderer;

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
        ValueApplier.applyValue(value, labeled.textProperty());
        return renderLabeled(labeled, autoWrap, ellipsis);
    }

    public static <L extends Labeled> L renderLabeled(L labeled, boolean autoWrap, boolean ellipsis) {
        if (autoWrap) {
            labeled.setWrapText(true);
            // Note that setting wrapText = true is not enough to make the labeled automatically wrap the text, the other
            // condition being to constraint either 1) the width or 2) the height of the labeled as follows:
            // 1) labeled.prefWidthProperty().bind(vBox.widthProperty());
            // 2) labeled.setMinHeight(Region.USE_PREF_SIZE);
            // We choose 2) as it doesn't need a reference to the container. In addition, it's not sure that the labeled
            // needs to have the exact same width as the container (it may have margin or be laid out differently).
            // So 2) is definitely more generic.
            labeled.setMinHeight(Region.USE_PREF_SIZE);
            // This is the general default behavior for "wrappedLabel" and "ellipsisLabel", but some renderers may
            // wish a different behavior. In that case, they can call removePossibleLabelAutoWrap() method (see for
            // example VisualGridTableSkin.setCellContent() method).
        }
        if (ellipsis)
            labeled.getStyleClass().add("ellipsis");
        return labeled;
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
