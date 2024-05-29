package dev.webfx.extras.cell.renderer;

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
    };

}
