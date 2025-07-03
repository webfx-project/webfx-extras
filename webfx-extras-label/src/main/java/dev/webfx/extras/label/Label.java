package dev.webfx.extras.label;

/**
 * Generic class to represent a Label with a text and an icon. It is used by VisualColumn for the column headers, but
 * also in the WebFX Stack domain model (DomainClass and DomainField), and the domain model is also used on the server
 * side. This is why this class has no dependency to JavaFX (in particular no StringProperty). But to support i18n on
 * the client side, the text field is declared as an Object (and not a String), so its value can be set to a String or
 * a StringProperty (see WebFX Stack EntitiesToVisualResultMapper for example).
 *
 * @author Bruno Salmon
 */
public final class Label {

    private final Object code; // Can be used as an i18n key on the client side
    private Object text; // can be a String or a StringProperty
    private final String iconPath;

    public static final Label emptyLabel = new Label("");

    public Label(Object text) {
        this(null, text, null);
    }

    public Label(String code, Object text, String iconPath) {
        this.code = code != null ? code : text; // temporary while all codes are not set
        this.text = text;
        this.iconPath = iconPath;
    }

    public Object getCode() {
        return code;
    }

    public Object getText() {
        return text;
    }

    public void setText(Object text) {
        this.text = text;
    }

    public String getIconPath() {
        return iconPath;
    }

    public static Label from(Object o) {
        if (o == null || "".equals(o))
            return emptyLabel;
        if (o instanceof Label)
            return (Label) o;
        if (o instanceof HasLabel)
            return ((HasLabel) o).getLabel();
/*
        if (o instanceof Symbol)
            return new Label(((Symbol) o).getName());
        if (o instanceof As)
            return new Label(((As) o).getAlias());
*/
        return new Label(o);
    }
}
