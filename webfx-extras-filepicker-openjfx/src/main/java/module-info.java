// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.filepicker.openjfx {

    // Direct dependencies modules
    requires java.base;
    requires javafx.graphics;
    requires webfx.extras.filepicker;
    requires webfx.platform.file;

    // Exported packages
    exports dev.webfx.extras.filepicker.spi.impl.openjfx;

    // Provided services
    provides dev.webfx.extras.filepicker.spi.FilePickerProvider with dev.webfx.extras.filepicker.spi.impl.openjfx.OpenJfxFilePickerProvider;

}