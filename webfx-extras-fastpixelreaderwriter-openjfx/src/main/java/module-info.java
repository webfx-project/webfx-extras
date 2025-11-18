// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.fastpixelreaderwriter.openjfx {

    // Direct dependencies modules
    requires javafx.graphics;
    requires webfx.extras.fastpixelreaderwriter;

    // Exported packages
    exports dev.webfx.extras.fastpixelreaderwriter.spi.impl.openjfx;

    // Provided services
    provides dev.webfx.extras.fastpixelreaderwriter.spi.FastPixelReaderWriterProvider with dev.webfx.extras.fastpixelreaderwriter.spi.impl.openjfx.OpenJFXFastPixelReaderWriterProvider;

}