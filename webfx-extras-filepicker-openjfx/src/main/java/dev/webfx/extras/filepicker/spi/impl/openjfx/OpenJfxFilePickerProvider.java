package dev.webfx.extras.filepicker.spi.impl.openjfx;

import dev.webfx.extras.filepicker.FilePicker;
import dev.webfx.extras.filepicker.spi.FilePickerProvider;

/**
 * @author Bruno Salmon
 */
public class OpenJfxFilePickerProvider implements FilePickerProvider {
    @Override
    public FilePicker createFileChooserPeer() {
        return new OpenJFXFilePicker();
    }
}
