// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.i18n {

    // Direct dependencies modules
    requires javafx.base;
    requires javafx.graphics;
    requires webfx.extras.fxraiser;
    requires webfx.extras.operation;
    requires webfx.kit.util;
    requires webfx.platform.async;
    requires webfx.platform.console;
    requires webfx.platform.scheduler;
    requires webfx.platform.service;
    requires webfx.platform.uischeduler;
    requires transitive webfx.platform.util;

    // Exported packages
    exports dev.webfx.extras.i18n;
    exports dev.webfx.extras.i18n.operations;
    exports dev.webfx.extras.i18n.spi;
    exports dev.webfx.extras.i18n.spi.impl;

    // Used services
    uses dev.webfx.extras.i18n.operations.ChangeLanguageRequestEmitter;
    uses dev.webfx.extras.i18n.spi.I18nProvider;

}