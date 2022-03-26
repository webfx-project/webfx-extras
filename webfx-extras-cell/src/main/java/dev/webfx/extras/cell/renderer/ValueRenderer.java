package dev.webfx.extras.cell.renderer;

import dev.webfx.extras.type.Type;
import dev.webfx.extras.cell.collator.NodeCollator;
import dev.webfx.extras.cell.collator.NodeCollatorRegistry;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public interface ValueRenderer {

    Node renderValue(Object value, ValueRenderingContext context);

    // If no context is passed, we use the default read-only context
    default Node renderValue(Object value) {
        return renderValue(value, ValueRenderingContext.DEFAULT_READONLY_CONTEXT);
    }

    static ValueRenderer create(Type type) {
        return ValueRendererFactory.getDefault().createValueRenderer(type);
    }

    static ValueRenderer create(Type type, String collator) {
        return create(type, NodeCollatorRegistry.getCollator(collator));
    }

    static ValueRenderer create(Type type, NodeCollator collator) {
        ValueRenderer renderer = create(type);
        if (collator != null && renderer instanceof ArrayRenderer)
            ((ArrayRenderer) renderer).setCollator(collator);
        return renderer;
    }
}
