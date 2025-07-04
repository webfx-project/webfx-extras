// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.validation {

    // Direct dependencies modules
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires webfx.extras.imagestore;
    requires webfx.extras.util.background;
    requires webfx.extras.util.border;
    requires webfx.extras.util.scene;
    requires webfx.kit.util;
    requires webfx.platform.uischeduler;
    requires webfx.platform.util;

    // Exported packages
    exports dev.webfx.extras.validation;
    exports dev.webfx.extras.validation.controlsfx.control.decoration;
    exports dev.webfx.extras.validation.controlsfx.impl;
    exports dev.webfx.extras.validation.controlsfx.impl.skin;
    exports dev.webfx.extras.validation.controlsfx.tools;
    exports dev.webfx.extras.validation.controlsfx.validation;
    exports dev.webfx.extras.validation.controlsfx.validation.decoration;
    exports dev.webfx.extras.validation.mvvmfx;
    exports dev.webfx.extras.validation.mvvmfx.visualization;

    // Resources packages
    opens dev.webfx.extras.validation.controlsfx.images;

}