// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.canvas.blob.openjfx {

    // Direct dependencies modules
    requires javafx.graphics;
    requires webfx.extras.canvas.blob;
    requires webfx.platform.async;
    requires webfx.platform.blob;

    // Exported packages
    exports dev.webfx.extras.canvas.blob.spi.impl.openjfx;

    // Provided services
    provides dev.webfx.extras.canvas.blob.spi.CanvasBlobProvider with dev.webfx.extras.canvas.blob.spi.impl.openjfx.OpenJFXCanvasBlobProvider;

}