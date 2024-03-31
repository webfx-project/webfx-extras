// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.webtext.registry {

    // Direct dependencies modules
    requires webfx.platform.service;

    // Exported packages
    exports dev.webfx.extras.webtext.registry;
    exports dev.webfx.extras.webtext.registry.spi;

    // Used services
    uses dev.webfx.extras.webtext.registry.spi.WebTextRegistryProvider;

}