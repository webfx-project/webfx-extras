package dev.webfx.extras.filepicker.spi.impl;

import dev.webfx.extras.filepicker.FilePicker;
import javafx.scene.Cursor;
import javafx.scene.layout.Region;

/**
 * @author Bruno Salmon
 */
public class FilePickerClickableRegion extends Region {

    private final FilePicker filePicker;

    public FilePickerClickableRegion(FilePicker filePicker) {
        this.filePicker = filePicker;
        setCursor(Cursor.HAND);
        //setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
    }

    public FilePicker getFileChooser() {
        return filePicker;
    }
}
