package dev.webfx.extras.cell.renderer;

import dev.webfx.extras.type.ArrayType;
import dev.webfx.extras.type.Type;
import dev.webfx.extras.type.Types;
import dev.webfx.extras.cell.collator.NodeCollator;
import dev.webfx.extras.cell.collator.NodeCollatorRegistry;
import dev.webfx.platform.util.Arrays;

/**
 * @author Bruno Salmon
 */
public final class GenericValueRendererFactory implements ValueRendererFactory {

    public final static GenericValueRendererFactory SINGLETON = new GenericValueRendererFactory();

    @Override
    public ValueRenderer createValueRenderer(Type type) {
        if (Types.isImageType(type)) // Case: image type
            return ImageRenderer.SINGLETON;
        // TODO: See if we can use a SPI instead to remove the dependency between this module and webfx-extras-webtext-controls
        if (Types.isHtmlType(type)) // Case: html type
            return HtmlTextRenderer.SINGLETON;
        if (Types.isArrayType(type)) { // Case: any array type (including image & text)
            Type[] types = ((ArrayType) type).getTypes();
            if (Arrays.length(types) == 2 && Types.isImageType(types[0])) // Case: image & text type
                return ImageTextRenderer.SINGLETON;
            // Case: any other array type
            ValueRenderer[] renderers = new ValueRenderer[types.length];
            for (int i = 0; i < types.length; i++)
                renderers[i] = createValueRenderer(types[i]);
            return new ArrayRenderer(renderers, getNodeCollator());
        }
        if (Types.isBooleanType(type))
            return BooleanRenderer.SINGLETON;
        // Text renderer is the default one
        return TextRenderer.SINGLETON;
    }

    private NodeCollator getNodeCollator() {
        return NodeCollatorRegistry.hBoxCollator();
    }
}
