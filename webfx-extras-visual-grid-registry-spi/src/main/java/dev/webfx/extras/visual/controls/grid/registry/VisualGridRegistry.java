package dev.webfx.extras.visual.controls.grid.registry;

import dev.webfx.extras.visual.controls.grid.registry.spi.VisualGridRegistryProvider;
import dev.webfx.platform.util.serviceloader.SingleServiceProvider;

import java.util.ServiceLoader;

public class VisualGridRegistry {

    private static VisualGridRegistryProvider getProvider() {
        return SingleServiceProvider.getProvider(VisualGridRegistryProvider.class, () -> ServiceLoader.load(VisualGridRegistryProvider.class));
    }

    public static void registerVisualGrid() {
        getProvider().registerVisualGrid();
    }

}
