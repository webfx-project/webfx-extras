// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.visual.controls.charts.peers.openjfx {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.web;
    requires webfx.extras.type;
    requires webfx.extras.visual.base;
    requires webfx.extras.visual.controls.charts;
    requires webfx.extras.visual.controls.charts.peers.base;
    requires webfx.extras.visual.controls.charts.registry;
    requires webfx.kit.javafxgraphics.peers;
    requires webfx.kit.openjfx;
    requires webfx.platform.util;

    // Exported packages
    exports dev.webfx.extras.visual.controls.charts.peers.openjfx;
    exports dev.webfx.extras.visual.controls.charts.registry.spi.impl.openjfx;

    // Provided services
    provides dev.webfx.extras.visual.controls.charts.registry.spi.VisualChartsRegistryProvider with dev.webfx.extras.visual.controls.charts.registry.spi.impl.openjfx.JavaFxVisualChartsRegistryProvider;

}