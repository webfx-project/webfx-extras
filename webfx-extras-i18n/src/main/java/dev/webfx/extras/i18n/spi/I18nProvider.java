package dev.webfx.extras.i18n.spi;

import dev.webfx.platform.util.collection.Collections;
import dev.webfx.platform.service.MultipleServiceProviders;
import dev.webfx.extras.i18n.Dictionary;
import dev.webfx.extras.i18n.TokenKey;
import dev.webfx.extras.i18n.operations.ChangeLanguageRequestEmitter;
import dev.webfx.extras.fxraiser.FXRaiser;
import dev.webfx.extras.fxraiser.FXValueRaiser;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;

import java.util.List;
import java.util.ServiceLoader;

/**
 * @author Bruno Salmon
 */
public interface I18nProvider {

    default List<Object> getSupportedLanguages() {
        return Collections.map(getProvidedInstantiators(), i -> i.emitLanguageRequest().getLanguage());
    }

    default List<ChangeLanguageRequestEmitter> getProvidedInstantiators() {
        return MultipleServiceProviders.getProviders(ChangeLanguageRequestEmitter.class, () -> ServiceLoader.load(ChangeLanguageRequestEmitter.class));
    }

    ObjectProperty<Object> languageProperty();
    default Object getLanguage() { return languageProperty().getValue(); }
    default void setLanguage(Object language) { languageProperty().setValue(language); }

    ObservableObjectValue<Dictionary> dictionaryProperty();
    default Dictionary getDictionary() {
        return dictionaryProperty().getValue();
    }

    Object getDefaultLanguage();
    Dictionary getDefaultDictionary();

    /// NEW API

    default <TK extends Enum<?> & TokenKey> Object getDictionaryTokenValue(Object i18nKey, TK tokenKey) {
        return getDictionaryTokenValue(i18nKey, tokenKey, null);
    }

    default <TK extends Enum<?> & TokenKey> Object getDictionaryTokenValue(Object i18nKey, TK tokenKey, Dictionary dictionary) {
        if (dictionary == null)
            dictionary = getDictionary();
        return dictionary.getMessageTokenValue(i18nKeyToDictionaryMessageKey(i18nKey), tokenKey, false); // Is it ok to always ignore case or should we add this to method signature
    }

    // Temporary (should be protected)
    default Object i18nKeyToDictionaryMessageKey(Object i18nKey) {
        if (i18nKey instanceof HasDictionaryMessageKey)
            return ((HasDictionaryMessageKey) i18nKey).getDictionaryMessageKey();
        return i18nKey;
    }

    default <TK extends Enum<?> & TokenKey> Object getUserTokenValue(Object i18nKey, TK tokenKey, Object... args) {
        return getUserTokenValue(i18nKey, tokenKey, getDictionary(), args);
    }

    default <TK extends Enum<?> & TokenKey> Object getUserTokenValue(Object i18nKey, TK tokenKey, Dictionary dictionary, Object... args) {
        Object dictionaryValue = getDictionaryTokenValue(i18nKey, tokenKey, dictionary);
        return FXRaiser.raiseToObject(dictionaryValue, tokenKey.expectedClass(), getI18nFxValueRaiser(), args);
    }

    <TK extends Enum<?> & TokenKey> ObservableValue<?> dictionaryTokenProperty(Object i18nKey, TK tokenKey, Object... args);

    default <TK extends Enum<?> & TokenKey> ObservableValue<?> userTokenProperty(Object i18nKey, TK tokenKey, Object... args) {
        return FXRaiser.raiseToProperty(dictionaryTokenProperty(i18nKey, tokenKey, args), tokenKey.expectedClass(), getI18nFxValueRaiser(), args);
    }

    default FXValueRaiser getI18nFxValueRaiser() {
        return null;
    }

    boolean refreshMessageTokenProperties(Object i18nKey);

    void scheduleMessageLoading(Object i18nKey, boolean inDefaultLanguage);


}
