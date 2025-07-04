package dev.webfx.extras.i18n.spi.impl.ast;


import dev.webfx.extras.i18n.spi.impl.I18nProviderImpl;

/**
 * @author Bruno Salmon
 */
public class AstI18nProvider extends I18nProviderImpl {

    public AstI18nProvider() {
        this(new String[]{"properties", "json"});
    }

    public AstI18nProvider(String... supportedFormats) {
        this("dev/webfx/extras/i18n/{lang}.{format}", supportedFormats);
    }

    public AstI18nProvider(String resourcePathWithLangPattern, String... supportedFormats) {
        this(resourcePathWithLangPattern, "en", supportedFormats);
    }

    public AstI18nProvider(String resourcePathWithLangPattern, Object defaultLanguage, String... supportedFormats) {
        this(resourcePathWithLangPattern, defaultLanguage, "en", supportedFormats);
    }

    public AstI18nProvider(String resourcePathWithLangPattern, Object defaultLanguage, Object initialLanguage, String... supportedFormats) {
        super(new ResourceAstDictionaryLoader(resourcePathWithLangPattern, supportedFormats), defaultLanguage, initialLanguage);
    }

}
