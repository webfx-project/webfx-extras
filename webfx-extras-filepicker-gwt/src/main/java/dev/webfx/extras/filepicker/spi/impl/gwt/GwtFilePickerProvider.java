package dev.webfx.extras.filepicker.spi.impl.gwt;

import dev.webfx.extras.filepicker.FilePicker;
import dev.webfx.extras.filepicker.spi.FilePickerProvider;

/**
 * @author Bruno Salmon
 */
public class GwtFilePickerProvider implements FilePickerProvider {

    @Override
    public FilePicker createFileChooserPeer() {
        return new GwtFilePicker();
    }
}
