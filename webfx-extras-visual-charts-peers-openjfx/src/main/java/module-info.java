// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.visual.charts.peers.openjfx {

    // Direct dependencies modules
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;
    requires webfx.extras.type;
    requires webfx.extras.visual;
    requires webfx.extras.visual.charts;
    requires webfx.extras.visual.charts.peers.base;
    requires webfx.extras.visual.charts.registry;
    requires webfx.kit.javafxgraphics.openjfx;
    requires webfx.kit.javafxgraphics.peers;
    requires webfx.platform.util;

    // Exported packages
    exports dev.webfx.extras.visual.controls.charts.peers.openjfx;
    exports dev.webfx.extras.visual.controls.charts.registry.spi.impl.openjfx;

    // Provided services
    provides dev.webfx.extras.visual.controls.charts.registry.spi.VisualChartsRegistryProvider with dev.webfx.extras.visual.controls.charts.registry.spi.impl.openjfx.OpenJFXVisualChartsRegistryProvider;

}