// File managed by WebFX (DO NOT EDIT MANUALLY)

import dev.webfx.extras.filepicker.spi.FilePickerProvider;

module webfx.extras.filepicker {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires javafx.graphics;
    requires webfx.kit.util;
    requires webfx.platform.file;
    requires webfx.platform.util;

    // Exported packages
    exports dev.webfx.extras.filepicker;
    exports dev.webfx.extras.filepicker.spi;
    exports dev.webfx.extras.filepicker.spi.impl;

    // Used services
    uses FilePickerProvider;

}