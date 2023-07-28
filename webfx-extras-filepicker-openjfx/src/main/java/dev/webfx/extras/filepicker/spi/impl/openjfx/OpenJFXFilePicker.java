package dev.webfx.extras.filepicker.spi.impl.openjfx;

import dev.webfx.extras.filepicker.spi.impl.BaseFilePicker;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public class OpenJFXFilePicker extends BaseFilePicker {

    private final FileChooser fileChooser = new FileChooser();

    public OpenJFXFilePicker() {
        filePickerClickableRegion.setOnMouseClicked(e -> {
            Window window = view.getScene().getWindow();
            List<File> files = null;
            if (isMultiple())
                files = fileChooser.showOpenMultipleDialog(window);
            else {
                File file = fileChooser.showOpenDialog(window);
                if (file != null)
                    files = Collections.singletonList(file);
            }
            if (files == null)
                getSelectedFiles().clear();
            else
                getSelectedFiles().setAll(files.stream()
                        .map(dev.webfx.platform.file.File::create)
                        .toArray(dev.webfx.platform.file.File[]::new));
        });
    }

}
