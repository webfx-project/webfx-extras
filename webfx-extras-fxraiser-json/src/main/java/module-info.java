// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.fxraiser.json {

    // Direct dependencies modules
    requires javafx.graphics;
    requires webfx.extras.fxraiser;
    requires webfx.extras.jsonimage;
    requires webfx.platform.ast;
    requires webfx.platform.ast.json.plugin;
    requires webfx.platform.boot;
    requires webfx.platform.util;

    // Exported packages
    exports dev.webfx.extras.fxraiser.json;

    // Provided services
    provides dev.webfx.platform.boot.spi.ApplicationModuleBooter with dev.webfx.extras.fxraiser.json.JsonFXRaiserModuleBooter;

}