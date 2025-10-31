package dev.webfx.extras.fastpixelreaderwriter.spi;

import dev.webfx.extras.fastpixelreaderwriter.FastPixelReaderWriter;
import javafx.scene.image.Image;

/**
 * @author Bruno Salmon
 */
public interface FastPixelReaderWriterProvider {

    FastPixelReaderWriter createFastPixelReaderWriter(Image image);

}
