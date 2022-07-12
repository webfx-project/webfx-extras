// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.webtext.controls.peers.openjfx {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.web;
    requires jdk.jsobject;
    requires webfx.extras.webtext.controls;
    requires webfx.extras.webtext.controls.peers.base;
    requires webfx.extras.webtext.controls.registry;
    requires webfx.kit.javafxgraphics.peers;
    requires webfx.kit.openjfx;
    requires webfx.platform.util;

    // Exported packages
    exports dev.webfx.extras.webtext.controls.peers.openjfx;
    exports dev.webfx.extras.webtext.controls.registry.spi.impl.openjfx;

    // Provided services
    provides dev.webfx.extras.webtext.controls.registry.spi.WebTextRegistryProvider with dev.webfx.extras.webtext.controls.registry.spi.impl.openjfx.JavaFxWebTextRegistryProvider;

}