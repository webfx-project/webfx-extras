// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.visual.controls.charts.registry {

    // Direct dependencies modules
    requires java.base;
    requires webfx.platform.util;

    // Exported packages
    exports dev.webfx.extras.visual.controls.charts.registry;
    exports dev.webfx.extras.visual.controls.charts.registry.spi;

    // Used services
    uses dev.webfx.extras.visual.controls.charts.registry.spi.VisualChartsRegistryProvider;

}