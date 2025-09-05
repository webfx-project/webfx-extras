package dev.webfx.extras.controlfactory.button;

import dev.webfx.extras.action.Action;
import dev.webfx.extras.util.control.Controls;
import dev.webfx.extras.validation.controlsfx.control.decoration.GraphicDecoration;
import dev.webfx.kit.util.properties.FXProperties;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

/**
 * @author Bruno Salmon
 */
public final class ButtonFactory {

    public static Button newButton(Action action) {
        return newButtonBuilder(action).build();
    }

    public static ButtonBuilder newButtonBuilder(Action action) {
        return new ButtonBuilder().setAction(action);
    }

    public static Button newButton(Object iconUrlOrJson, Object translationKey, EventHandler<ActionEvent> onAction) {
        return newButtonBuilder(iconUrlOrJson, translationKey, onAction).build();
    }

    public static ButtonBuilder newButtonBuilder(Object iconUrlOrJson, Object translationKey, EventHandler<ActionEvent> onAction) {
        return new ButtonBuilder().setIconUrlOrJson(iconUrlOrJson).setI18nKey(translationKey).setOnAction(onAction);
    }

    public static Button newButton(Node graphic, Object translationKey, EventHandler<ActionEvent> onAction) {
        return newButtonBuilder(graphic, translationKey, onAction).build();
    }

    public static ButtonBuilder newButtonBuilder(Node graphic, Object translationKey, EventHandler<ActionEvent> onAction) {
        return new ButtonBuilder().setIcon(graphic).setI18nKey(translationKey).setOnAction(onAction);
    }

    public static Button newDropDownButton() {
        Button button = new Button();
        button.getStyleClass().add("drop-down-button");
        return decorateButtonWithDropDownArrow(button);
    }

    public static Button decorateButtonWithDropDownArrow(Button button) {
        SVGPath downArrow = new SVGPath();
        downArrow.setStrokeWidth(0.71);
        downArrow.setFill(null);
        downArrow.setContent("M1 1.22998L6.325 6.55499L11.65 1.22998");
        GraphicDecoration dropDownArrowDecoration = new GraphicDecoration(downArrow, Pos.CENTER_RIGHT, 0, 0, -1.4, 0);
        FXProperties.runNowAndOnPropertyChange(() -> Platform.runLater(() ->
            Controls.onSkinReady(button, () -> dropDownArrowDecoration.applyDecoration(button))
        ), button.graphicProperty());
        // Code to clip the content before the down arrow
        FXProperties.runNowAndOnPropertiesChange(() -> {
            // Adding padding for the extra right icon decoration (adding the icon width 16px + repeating the 6px standard padding)
            double rightDecorationWidth = button.getHeight();
            //button.setPadding(new Insets(3, 6 + rightDecorationWidth + 6, 3, 6));
            Node graphic = button.getGraphic();
            if (graphic != null) {
                graphic.setClip(new Rectangle(0, 0, button.getWidth() - rightDecorationWidth, button.getHeight()));
            }
        }, downArrow.layoutXProperty(), button.graphicProperty(), button.heightProperty());
        button.setMinWidth(0d);
        button.setMaxWidth(Double.MAX_VALUE);
        // Adding padding for the extra right icon decoration (adding the icon width 16px + repeating the 6px standard padding)
        button.setPadding(new Insets(3, 6 + 20 + 6, 3, 6));
        button.setAlignment(Pos.CENTER_LEFT);
        return button;
    }

    public static void resetDefaultButton(Button button) {
        // Resetting a default button which is required for JavaFX for the cases when the button is displayed a second time
        button.setDefaultButton(false);
        button.setDefaultButton(true);
    }

    public static void resetCancelButton(Button button) {
        // Resetting a cancel button which is required for JavaFX for the cases when the button is displayed a second time
        button.setCancelButton(false);
        button.setCancelButton(true);
    }

    public static void resetDefaultAndCancelButtons(Button defaultButton, Button cancelButton) {
        resetDefaultButton(defaultButton);
        resetCancelButton(cancelButton);
    }

}
