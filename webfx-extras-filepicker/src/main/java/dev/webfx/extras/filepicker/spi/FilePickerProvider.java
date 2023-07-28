package dev.webfx.extras.filepicker.spi;

import dev.webfx.extras.filepicker.FilePicker;
import dev.webfx.platform.util.serviceloader.SingleServiceProvider;

import java.util.ServiceLoader;

/**
 * @author Bruno Salmon
 */
public interface FilePickerProvider {

    static FilePickerProvider get() {
        return SingleServiceProvider.getProvider(FilePickerProvider.class, () -> ServiceLoader.load(FilePickerProvider.class));
    }

    FilePicker createFileChooserPeer();

}
