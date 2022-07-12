// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.webtext.registry {

    // Direct dependencies modules
    requires java.base;
    requires webfx.platform.util;

    // Exported packages
    exports dev.webfx.extras.webtext.registry;
    exports dev.webfx.extras.webtext.registry.spi;

    // Used services
    uses dev.webfx.extras.webtext.registry.spi.WebTextRegistryProvider;

}