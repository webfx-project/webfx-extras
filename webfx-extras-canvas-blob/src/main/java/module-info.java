// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.canvas.blob {

    // Direct dependencies modules
    requires javafx.graphics;
    requires webfx.platform.async;
    requires webfx.platform.blob;
    requires webfx.platform.service;

    // Exported packages
    exports dev.webfx.extras.canvas.blob;
    exports dev.webfx.extras.canvas.blob.spi;

    // Used services
    uses dev.webfx.extras.canvas.blob.spi.CanvasBlobProvider;

}