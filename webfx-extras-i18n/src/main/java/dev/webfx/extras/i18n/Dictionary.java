package dev.webfx.extras.i18n;

/**
 * @author Bruno Salmon
 */
public interface Dictionary {

    <TK extends Enum<?> & TokenKey> Object getMessageTokenValue(Object messageKey, TK tokenKey, boolean ignoreCase);

}
