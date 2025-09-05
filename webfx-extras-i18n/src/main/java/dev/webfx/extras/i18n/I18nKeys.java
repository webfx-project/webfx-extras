package dev.webfx.extras.i18n;

/**
 * @author Bruno Salmon
 */
public final class I18nKeys {

    public static String appendEllipsis(Object i18nKey) {
        return i18nKey + "...";
    }

    public static String appendColons(Object i18nKey) {
        return i18nKey + ":";
    }

    public static String appendArrows(Object i18nKey) {
        return i18nKey + ">>";
    }

    public static String prependArrows(Object i18nKey) {
        return "<<" + i18nKey;
    }

    public static String upperCase(Object i18nKey) {
        return i18nKey.toString().toUpperCase();
    }

    public static String lowerCase(Object i18nKey) {
        return i18nKey.toString().toLowerCase();
    }

    public static Object upperCaseFirstChar(Object i18nKey) {
        String i18nKeyString = i18nKey.toString();
        char firstCharKey = i18nKeyString.charAt(0);
        if (!Character.isUpperCase(firstCharKey))
            i18nKey = Character.toUpperCase(firstCharKey) + i18nKeyString.substring(1);
        return i18nKey;
    }

    public static Object lowerCaseFirstChar(Object i18nKey) {
        String i18nKeyString = i18nKey.toString();
        char firstCharKey = i18nKeyString.charAt(0);
        if (!Character.isLowerCase(firstCharKey))
            i18nKey = Character.toLowerCase(firstCharKey) + i18nKeyString.substring(1);
        return i18nKey;
    }

    public static String embedInString(Object i18nKey) {
        return "[" + i18nKey + "]";
    }

    public static String embedInString(String text, Object i18nKey) {
        return text.replace("[0]", embedInString(i18nKey));
    }

}
