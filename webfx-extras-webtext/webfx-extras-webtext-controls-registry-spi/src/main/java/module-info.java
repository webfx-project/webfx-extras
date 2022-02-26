// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.webtext.controls.registry {

    // Direct dependencies modules
    requires java.base;
    requires webfx.platform.shared.util;

    // Exported packages
    exports dev.webfx.extras.webtext.controls.registry;
    exports dev.webfx.extras.webtext.controls.registry.spi;

    // Used services
    uses dev.webfx.extras.webtext.controls.registry.spi.WebTextRegistryProvider;

}