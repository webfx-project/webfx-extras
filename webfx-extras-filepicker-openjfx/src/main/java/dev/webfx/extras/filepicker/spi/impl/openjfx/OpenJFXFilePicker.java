package dev.webfx.extras.filepicker.spi.impl.openjfx;

import dev.webfx.extras.filepicker.spi.impl.BaseFilePicker;
import dev.webfx.platform.util.collection.Collections;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public class OpenJFXFilePicker extends BaseFilePicker {

    private final FileChooser fileChooser = new FileChooser();

    public OpenJFXFilePicker() {
        filePickerClickableRegion.setOnMouseClicked(e -> {
            if (getAcceptedExtensions().isEmpty())
                fileChooser.getExtensionFilters().clear();
            else
                fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter(String.join(",", getAcceptedExtensions()), toOpenJFXExtensions(getAcceptedExtensions())));
            Window window = view.getScene().getWindow();
            List<File> files = null;
            if (isMultiple())
                files = fileChooser.showOpenMultipleDialog(window);
            else {
                File file = fileChooser.showOpenDialog(window);
                if (file != null)
                    files = java.util.Collections.singletonList(file);
            }
            if (files == null)
                getSelectedFiles().clear();
            else
                getSelectedFiles().setAll(files.stream()
                        .map(dev.webfx.platform.file.File::create)
                        .toArray(dev.webfx.platform.file.File[]::new));
        });
    }

    private static List<String> toOpenJFXExtensions(List<String> acceptedExtensions) {
        List<String> openJFXExtensions = new ArrayList<>();
        for (String acceptedExtension : acceptedExtensions) {
            // OpenJFX doesn't accept MIME types (as opposed to browser), so we translate them to extensions
            if (acceptedExtension.equalsIgnoreCase("image/*"))
                Collections.addAll(openJFXExtensions, "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp");
            else if (acceptedExtension.equalsIgnoreCase("audio/*"))
                Collections.addAll(openJFXExtensions,"*.mp3", "*.wav", "*.aiff", "*.au", "*.midi", "*.aac", "*.ogg", "*.flac");
            else if (acceptedExtension.equalsIgnoreCase("video/*"))
                Collections.addAll(openJFXExtensions,"*.mp4", "*.avi", "*.mov", "*.flv", "*.wmv", "*.webm", "*.mkv");
            else
                openJFXExtensions.add(acceptedExtension);
        }
        return openJFXExtensions;
    }

}
