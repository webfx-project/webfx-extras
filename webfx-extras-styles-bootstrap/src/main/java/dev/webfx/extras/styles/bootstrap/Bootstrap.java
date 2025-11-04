package dev.webfx.extras.styles.bootstrap;


import dev.webfx.platform.util.collection.Collections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Region;

/**
 * This interface is not essential but provides a list of css classes that the application code can use. A secondary
 * benefit of using it is that the WebFX CLI will automatically include the bootstrap module by detecting its usage
 * from the source code.
 *
 * Alternatively, the application code can simply apply the bootstrap css classes directly to the nodes without using
 * this class, but it will be then necessary to explicitly add the dependency to this bootstrap module in webfx.xml
 * to include the css rules declared in this module to the application.
 *
 * @author Bruno Salmon
 */
public interface Bootstrap {

    /* Bootstrap style */
    String BTN = "btn";
    // TODO: remove btn-xxx classes (use btn & xxx combination instead)
    String BTN_LG = "btn-lg";
    String BTN_SM = "btn-sm";
    String BTN_XS = "btn-xs";
    String BTN_DEFAULT = "btn-default";
    String BTN_PRIMARY = "btn-primary";
    String BTN_SECONDARY = "btn-secondary";
    String BTN_SUCCESS = "btn-success";
    String BTN_INFO = "btn-info";
    String BTN_WARNING = "btn-warning";
    String BTN_DANGER = "btn-danger";

    String BADGE = "badge";

    String ALERT = "alert";
    String ALERT_INFO = "alert-info";
    String ALERT_SUCCESS = "alert-success";
    String ALERT_WARNING = "alert-warning";
    String ALERT_DANGER = "alert-danger";
    String INFO_BOX = "info-box";

    String TEXT = "txt"; // Note: txt is used instead of text to avoid conflict with javafx.scene.text.Text

    // TODO: remove text-xxx classes (use text & xxx combination instead)
    String TEXT_PRIMARY = "txt-primary";
    String TEXT_SECONDARY = "txt-secondary";
    String TEXT_SUCCESS = "txt-success";
    String TEXT_INFO = "txt-info";
    String TEXT_WARNING = "txt-warning";
    String TEXT_DANGER = "txt-danger";

    String DEFAULT = "default"; // TODO: remove?
    String PRIMARY = "primary";
    String SECONDARY = "secondary";
    String SUCCESS = "success";
    String INFO = "info";
    String WARNING = "warning";
    String DANGER = "danger";

    String LG = "lg";
    String SM = "sm";
    String XS = "xs";

    String H1 = "h1";
    String H2 = "h2";
    String H3 = "h3";
    String H4 = "h4";
    String H5 = "h5";
    String H6 = "h6";

    String SMALL = "small";
    String STRONG = "strong";

    double DEFAULT_H_PADDING = 12;
    double DEFAULT_V_PADDING = 6;

    double LARGE_H_PADDING = 16;
    double LARGE_V_PADDING = 10;

    double BADGE_H_PADDING = 8;
    double BADGE_V_PADDING = 3;

    static <N extends Node> N style(N node, String... styles) {
        Collections.addIfNotContainsOrRemove(node.getStyleClass(), true, styles);
        return node;
    }

    static String[] combineStyles(String firstStyle, String... additionalStyles) {
        String[] combined = new String[additionalStyles.length + 1];
        combined[0] = firstStyle;
        System.arraycopy(additionalStyles, 0, combined, 1, additionalStyles.length);
        return combined;
    }

    static <N extends Node> N padding(N node, String... styles) {
        return padding(node, DEFAULT_H_PADDING, DEFAULT_V_PADDING, styles);
    }

    static <N extends Node> N padding(N node, double hPadding, double vPadding, String... styles) {
        if (node instanceof Region) {
            Region region = (Region) node;
            region.setPadding(new Insets(vPadding, hPadding, vPadding, hPadding));
        }
        return style(node, styles);
    }

    static <N extends Node> N badgePadding(N badge, String... styles) {
        return padding(badge, BADGE_H_PADDING, BADGE_V_PADDING, styles);
    }

    static <N extends Node> N large(N node) {
        return style(node, LG);
    }

    static <N extends Node> N largeResize(N node, String... styles) {
        return largeResize(node, true, styles);
    }

    static <N extends Node> N largeResize(N node, boolean largeHeight, String... styles) {
        if (node instanceof Region) {
            Region region = (Region) node;
            region.setMinWidth(240);
        }
        if (node instanceof Labeled) {
            ((Labeled) node).setGraphicTextGap(30);
        }
        return large(padding(node, LARGE_H_PADDING, largeHeight ? LARGE_V_PADDING : DEFAULT_V_PADDING, styles));
    }

    static <N extends Node> N addButtonStyle(N button) {
        return style(button, BTN);
    }

    static <N extends Node> N button(N button, String... styles) {
        return addButtonStyle(padding(button, styles));
    }

    static <N extends Node> N button(N button, double hPadding, double vPadding, String... styles) {
        return addButtonStyle(padding(button, hPadding, vPadding, styles));
    }

    static <N extends Node> N badge(N badge, String... styles) {
        return addBadgeStyle(badgePadding(badge, styles));
    }


    static <N extends Node> N addBadgeStyle(N badge) {
        return style(badge, BADGE);
    }

    static <N extends Node> N successBadge(N badge) {
        return badge(badge, SUCCESS);
    }

    static <N extends Node> N primaryBadge(N badge) {
        return badge(badge, PRIMARY);
    }

    static <N extends Node> N secondaryBadge(N badge) {
        return badge(badge, SECONDARY);
    }

    static <N extends Node> N dangerBadge(N badge) {
        return badge(badge, DANGER);
    }

    static <N extends Node> N warningBadge(N badge) {
        return badge(badge, WARNING);
    }

    // Alert / Info Box styles with padding
    static <N extends Node> N alertInfo(N node) {
        if (node instanceof Region) {
            ((Region) node).setPadding(new Insets(16));
        }
        return style(node, ALERT_INFO);
    }

    static <N extends Node> N alertSuccess(N node) {
        if (node instanceof Region) {
            ((Region) node).setPadding(new Insets(16));
        }
        return style(node, ALERT_SUCCESS);
    }

    static <N extends Node> N alertWarning(N node) {
        if (node instanceof Region) {
            ((Region) node).setPadding(new Insets(16));
        }
        return style(node, ALERT_WARNING);
    }

    static <N extends Node> N alertDanger(N node) {
        if (node instanceof Region) {
            ((Region) node).setPadding(new Insets(16));
        }
        return style(node, ALERT_DANGER);
    }

    static <N extends Node> N infoBox(N node) {
        if (node instanceof Region) {
            ((Region) node).setPadding(new Insets(16));
        }
        return style(node, INFO_BOX);
    }

    static <N extends Node> N largeButton(N button, String... styles) {
        return addButtonStyle(largeResize(button, styles));
    }

    static <N extends Node> N largeButton(N button, boolean largeHeight, String... styles) {
        return addButtonStyle(largeResize(button, largeHeight, styles));
    }

    static <N extends Node> N successButton(N button) {
        return button(button, SUCCESS);
    }

    static <N extends Node> N primaryButton(N button) {
        return button(button, PRIMARY);
    }

    static <N extends Node> N secondaryButton(N button) {
        return button(button, SECONDARY);
    }

    static <N extends Node> N dangerButton(N button) {
        return button(button, DANGER);
    }

    static <N extends Node> N largeSuccessButton(N button) {
        return largeSuccessButton(button, true);
    }

    static <N extends Node> N largeSuccessButton(N button, boolean largeHeight) {
        return largeButton(button, largeHeight, SUCCESS);
    }

    static <N extends Node> N largePrimaryButton(N button) {
        return largePrimaryButton(button, true);
    }

    static <N extends Node> N largePrimaryButton(N button, boolean largeHeight) {
        return largeButton(button, largeHeight, PRIMARY);
    }

    static <N extends Node> N largeSecondaryButton(N button) {
        return largeSecondaryButton(button, true);
    }

    static <N extends Node> N largeSecondaryButton(N button, boolean largeHeight) {
        return largeButton(button, largeHeight, SECONDARY);
    }

    static <N extends Node> N largeDangerButton(N button) {
        return largeButton(button, DANGER);
    }

    static <N extends Node> N h1(N node) {
        return style(node, H1);
    }

    static <N extends Node> N h2(N node) {
        return style(node, H2);
    }

    static <N extends Node> N h3(N node) {
        return style(node, H3);
    }

    static <N extends Node> N h4(N node) {
        return style(node, H4);
    }

    static <N extends Node> N h5(N node) {
        return style(node, H5);
    }

    static <N extends Node> N h6(N node) {
        return style(node, H6);
    }

    static <N extends Node> N textPrimary(N node) {
        return style(node, TEXT, PRIMARY);
    }

    static <N extends Node> N textSecondary(N node) {
        return style(node, TEXT, SECONDARY);
    }

    static <N extends Node> N textSuccess(N node) {
        return style(node, TEXT, SUCCESS);
    }

    static <N extends Node> N textWarning(N node) {
        return style(node, TEXT, WARNING);
    }

    static <N extends Node> N textDanger(N node) {
        return style(node, TEXT, DANGER);
    }

    static <N extends Node> N strong(N node) {
        return style(node, STRONG);
    }

    static <N extends Node> N small(N node) {
        return style(node, SMALL);
    }

    static <N extends Node> N h1Primary(N node) {
        return style(node, PRIMARY, H1);
    }

    static <N extends Node> N h2Primary(N node) {
        return style(node, PRIMARY, H2);
    }

}
