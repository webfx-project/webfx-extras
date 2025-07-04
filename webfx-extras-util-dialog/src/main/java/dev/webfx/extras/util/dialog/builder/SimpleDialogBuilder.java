package dev.webfx.extras.util.dialog.builder;

import dev.webfx.extras.util.dialog.DialogCallback;
import javafx.scene.layout.Region;

/**
 * @author Bruno Salmon
 */
public class SimpleDialogBuilder implements DialogBuilder {

    private final Region content;
    private DialogCallback dialogCallback;

    public SimpleDialogBuilder(Region content) {
        this.content = content;
    }

    @Override
    public Region build() {
        return content;
    }

    @Override
    public void setDialogCallback(DialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Override
    public DialogCallback getDialogCallback() {
        return dialogCallback;
    }
}
