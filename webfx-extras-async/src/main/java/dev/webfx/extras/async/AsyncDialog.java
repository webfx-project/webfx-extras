package dev.webfx.extras.async;

import dev.webfx.extras.exceptions.UserCancellationException;
import dev.webfx.extras.util.alert.AlertUtil;
import dev.webfx.extras.util.dialog.DialogCallback;
import dev.webfx.extras.util.dialog.builder.DialogBuilderUtil;
import dev.webfx.extras.util.dialog.builder.DialogContent;
import dev.webfx.platform.async.Future;
import dev.webfx.platform.async.Promise;
import dev.webfx.platform.uischeduler.UiScheduler;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
public final class AsyncDialog {

    public static Future<Void> showDialogWithAsyncOperationOnPrimaryButton(DialogContent dialogContent, Pane parentContainer, Supplier<Future<?>> executor) {
        Promise<Void> promise = Promise.promise();
        UiScheduler.runInUiThread(() -> {
            boolean[] executing = {false};
            DialogBuilderUtil.showModalNodeInGoldLayout(dialogContent, parentContainer)
                .addCloseHook(() -> {
                    if (!executing[0])
                        promise.fail(new UserCancellationException());
                });
            DialogBuilderUtil.armDialogContentButtons(dialogContent, dialogCallback -> {
                executing[0] = true;
                Button executingButton = dialogContent.getPrimaryButton();
                Button cancelButton = dialogContent.getSecondaryButton();
                AsyncSpinner.displayButtonSpinnerDuringAsyncExecution(
                    executor.get()
                        .inUiThread()
                        .onFailure(cause -> {
                            promise.fail(cause);
                            reportException(dialogCallback, parentContainer, cause); // Actually, just print stack trace for now...
                            if (dialogCallback != null) // So we close the window
                                dialogCallback.closeDialog();
                        })
                        .onSuccess(b -> {
                            promise.complete();
                            if (dialogCallback != null)
                                dialogCallback.closeDialog();
                        })
                    , executingButton, cancelButton);
            });
        });
        return promise.future();
    }

    private static void reportException(DialogCallback dialogCallback, Pane parentContainer, Throwable cause) {
        if (dialogCallback != null)
            dialogCallback.showException(cause);
        else
            AlertUtil.showExceptionAlert(cause, parentContainer.getScene().getWindow());
    }
}
