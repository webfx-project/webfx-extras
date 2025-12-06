package dev.webfx.extras.async;

import dev.webfx.extras.util.control.Controls;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.async.Future;
import javafx.scene.Node;
import javafx.scene.control.Labeled;

/**
 * Utility class for displaying a spinner in a button during an asynchronous operation.
 *
 * @author Bruno Salmon
 */
public final class AsyncSpinner {

    private static final String BUTTON_GRAPHIC_MEMO_PROPERTIES_KEY = "webfx-operation-util-graphic";

    // During execution, the first passed button will show a progress indicator, and all buttons will be disabled.
    // At the end of the execution, all buttons will be enabled again, and the first button graphic will be reset.

    // One issue with these methods is that it unbinds the button graphic property (which is ok during execution) but
    // doesn't reestablish the initial binding at the end (the initial graphic is just reset).

    public static void displayButtonSpinnerDuringAsyncExecution(Future<?> future, Labeled... buttons) {
        displayButtonSpinner(buttons);
        future
            .inUiThread()
            .onComplete(ignored -> hideButtonSpinner(buttons));
    }

    public static void displayButtonSpinner(Labeled... buttons) {
        displayButtonSpinner(true, buttons);
    }

    public static void hideButtonSpinner(Labeled... buttons) {
        displayButtonSpinner(false, buttons);
    }

    private static void displayButtonSpinner(boolean display, Labeled... buttons) {
        for (Labeled button : buttons) {
            FXProperties.setIfNotBound(button.disableProperty(), display);
            Node graphic = null;
            if (button == buttons[0]) {
                if (display) {
                    graphic = Controls.createSpinner(20);
                    // Memorizing the previous graphic before changing it
                    button.getProperties().put(BUTTON_GRAPHIC_MEMO_PROPERTIES_KEY, button.getGraphic());
                } else {
                    // Restoring the previous graphic once wait mode is turned off
                    graphic = (Node) button.getProperties().get(BUTTON_GRAPHIC_MEMO_PROPERTIES_KEY);
                }
            }
            FXProperties.setEvenIfBound(button.graphicProperty(), graphic);
        }
    }
}
