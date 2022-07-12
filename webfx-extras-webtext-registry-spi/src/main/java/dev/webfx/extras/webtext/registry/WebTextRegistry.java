package dev.webfx.extras.webtext.registry;

import dev.webfx.extras.webtext.registry.spi.WebTextRegistryProvider;
import dev.webfx.platform.util.serviceloader.SingleServiceProvider;

import java.util.ServiceLoader;

public class WebTextRegistry {

    private static WebTextRegistryProvider getProvider() {
        return SingleServiceProvider.getProvider(WebTextRegistryProvider.class, () -> ServiceLoader.load(WebTextRegistryProvider.class));
    }

    public static void registerHtmlText() {
        getProvider().registerHtmlText();
    }

    public static void registerHtmlTextEditor() {
        getProvider().registerHtmlTextEditor();
    }

    public static void registerSvgText() {
        getProvider().registerSvgText();
    }

}
