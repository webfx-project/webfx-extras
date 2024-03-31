// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.visual.charts.registry {

    // Direct dependencies modules
    requires webfx.platform.service;

    // Exported packages
    exports dev.webfx.extras.visual.controls.charts.registry;
    exports dev.webfx.extras.visual.controls.charts.registry.spi;

    // Used services
    uses dev.webfx.extras.visual.controls.charts.registry.spi.VisualChartsRegistryProvider;

}