package dev.webfx.extras.filepicker.spi.impl.gwtj2cl;

import dev.webfx.extras.filepicker.FilePicker;
import dev.webfx.extras.filepicker.spi.FilePickerProvider;

/**
 * @author Bruno Salmon
 */
public class GwtJ2clFilePickerProvider implements FilePickerProvider {

    @Override
    public FilePicker createFileChooserPeer() {
        return new GwtJ2clFilePicker();
    }
}
