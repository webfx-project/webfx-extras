// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.visual.controls.grid.registry {

    // Direct dependencies modules
    requires java.base;
    requires webfx.platform.shared.util;

    // Exported packages
    exports dev.webfx.extras.visual.controls.grid.registry;
    exports dev.webfx.extras.visual.controls.grid.registry.spi;

    // Used services
    uses dev.webfx.extras.visual.controls.grid.registry.spi.VisualGridRegistryProvider;

}