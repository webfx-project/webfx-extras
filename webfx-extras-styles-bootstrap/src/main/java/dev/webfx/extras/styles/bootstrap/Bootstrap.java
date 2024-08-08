package dev.webfx.extras.styles.bootstrap;


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

    String TEXT_PRIMARY = "text-primary";
    String TEXT_SECONDARY = "text-secondary";
    String TEXT_SUCCESS = "text-success";
    String TEXT_INFO = "text-info";
    String TEXT_WARNING = "text-warning";
    String TEXT_DANGER = "text-danger";

    String H1 = "h1";
    String H2 = "h2";
    String H3 = "h3";
    String H4 = "h4";
    String H5 = "h5";
    String H6 = "h6";

    String SMALL = "small";

    String STRONG = "strong";

    private static <N extends Node> N style(N node, String style) {
        node.getStyleClass().add(style);
        return node;
    }

    static <N extends Node> N button(N button) {
        return style(button, BTN);
    }

    static <N extends Node> N largeButton(N button) {
        if (button instanceof Region) {
            Region region = (Region) button;
            region.setMinWidth(150);
            region.setPadding(new Insets(15));
            if (button instanceof Labeled) {
                ((Labeled) button).setGraphicTextGap(30);
            }
        }
        return style(button(button), BTN_LG);
    }

    static <N extends Node> N successButton(N button) {
        return style(button(button), BTN_SUCCESS);
    }

    static <N extends Node> N primaryButton(N button) {
        return style(button(button), BTN_PRIMARY);
    }

    static <N extends Node> N secondaryButton(N button) {
        return style(button(button), BTN_SECONDARY);
    }

    static <N extends Node> N dangerButton(N button) {
        return style(button(button), BTN_DANGER);
    }

    static <N extends Node> N largeSuccessButton(N button) {
        return style(largeButton(button), BTN_SUCCESS);
    }

    static <N extends Node> N largePrimaryButton(N button) {
        return style(largeButton(button), BTN_PRIMARY);
    }

    static <N extends Node> N largeSecondaryButton(N button) {
        return style(largeButton(button), BTN_SECONDARY);
    }

    static <N extends Node> N largeDangerButton(N button) {
        return style(largeButton(button), BTN_DANGER);
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
        return style(node, TEXT_PRIMARY);
    }

    static <N extends Node> N textSecondary(N node) {
        return style(node, TEXT_SECONDARY);
    }

    static <N extends Node> N textSuccess(N node) {
        return style(node, TEXT_SUCCESS);
    }

    static <N extends Node> N textWarning(N node) {
        return style(node, TEXT_WARNING);
    }

    static <N extends Node> N textDanger(N node) {
        return style(node, TEXT_DANGER);
    }

    static <N extends Node> N strong(N node) {
        return style(node, STRONG);
    }

    static <N extends Node> N small(N node) {
        return style(node, SMALL);
    }

}
