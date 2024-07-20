// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.time.format {

    // Direct dependencies modules
    requires javafx.base;
    requires webfx.platform.service;

    // Exported packages
    exports dev.webfx.extras.time.format;
    exports dev.webfx.extras.time.format.spi;
    exports dev.webfx.extras.time.format.spi.impl;

    // Used services
    uses dev.webfx.extras.time.format.spi.TimeFormatProvider;

    // Provided services
    provides dev.webfx.extras.time.format.spi.TimeFormatProvider with dev.webfx.extras.time.format.spi.impl.TimeFormatProviderImpl;

}