// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.extras.i18n.ast.provider {

    // Direct dependencies modules
    requires javafx.base;
    requires webfx.extras.i18n;
    requires webfx.extras.i18n.ast;

    // Exported packages
    exports dev.webfx.extras.i18n.spi.impl.ast.provider;

    // Provided services
    provides dev.webfx.extras.i18n.spi.I18nProvider with dev.webfx.extras.i18n.spi.impl.ast.provider.Ast18nProvider;

}