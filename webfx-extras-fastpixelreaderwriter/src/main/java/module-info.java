// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.fastpixelreaderwriter {

    // Direct dependencies modules
    requires javafx.graphics;
    requires webfx.platform.service;

    // Exported packages
    exports dev.webfx.extras.fastpixelreaderwriter;
    exports dev.webfx.extras.fastpixelreaderwriter.spi;

    // Used services
    uses dev.webfx.extras.fastpixelreaderwriter.spi.FastPixelReaderWriterProvider;

}