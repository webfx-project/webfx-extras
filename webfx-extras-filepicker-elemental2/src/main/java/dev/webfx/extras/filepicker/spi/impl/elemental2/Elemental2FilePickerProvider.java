package dev.webfx.extras.filepicker.spi.impl.elemental2;

import dev.webfx.extras.filepicker.FilePicker;
import dev.webfx.extras.filepicker.spi.FilePickerProvider;

/**
 * @author Bruno Salmon
 */
public class Elemental2FilePickerProvider implements FilePickerProvider {

    @Override
    public FilePicker createFileChooserPeer() {
        return new Elemental2FilePicker();
    }
}
