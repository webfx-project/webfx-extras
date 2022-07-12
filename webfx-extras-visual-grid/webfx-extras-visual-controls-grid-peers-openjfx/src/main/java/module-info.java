// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.visual.controls.grid.peers.openjfx {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires webfx.extras.cell;
    requires webfx.extras.imagestore;
    requires webfx.extras.label;
    requires webfx.extras.visual.base;
    requires webfx.extras.visual.controls.grid;
    requires webfx.extras.visual.controls.grid.peers.base;
    requires webfx.extras.visual.controls.grid.registry;
    requires webfx.kit.javafxgraphics.peers;
    requires webfx.kit.openjfx;
    requires webfx.kit.util;
    requires webfx.platform.uischeduler;
    requires webfx.platform.util;

    // Exported packages
    exports dev.webfx.extras.visual.controls.grid.peers.openjfx;
    exports dev.webfx.extras.visual.controls.grid.registry.spi.openjfx;

    // Provided services
    provides dev.webfx.extras.visual.controls.grid.registry.spi.VisualGridRegistryProvider with dev.webfx.extras.visual.controls.grid.registry.spi.openjfx.JavaFxVisualGridRegistryProvider;

}