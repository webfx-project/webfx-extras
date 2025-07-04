// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.controlfactory {

    // Direct dependencies modules
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires transitive webfx.extras.action;
    requires webfx.extras.i18n;
    requires webfx.extras.i18n.controls;
    requires webfx.extras.jsonimage;
    requires webfx.extras.styles.materialdesign;
    requires webfx.extras.util.background;
    requires webfx.extras.util.border;
    requires webfx.extras.util.control;
    requires webfx.extras.util.layout;
    requires webfx.extras.util.paint;
    requires webfx.extras.validation;
    requires webfx.kit.util;

    // Exported packages
    exports dev.webfx.extras.controlfactory;
    exports dev.webfx.extras.controlfactory.button;

    // Resources packages
    opens images.s16.controls;

}