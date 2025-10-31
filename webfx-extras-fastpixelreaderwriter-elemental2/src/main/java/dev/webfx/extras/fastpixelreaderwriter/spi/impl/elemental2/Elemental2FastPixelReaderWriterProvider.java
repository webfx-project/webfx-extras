package dev.webfx.extras.fastpixelreaderwriter.spi.impl.elemental2;

import dev.webfx.extras.fastpixelreaderwriter.FastPixelReaderWriter;
import dev.webfx.extras.fastpixelreaderwriter.spi.FastPixelReaderWriterProvider;
import javafx.scene.image.Image;

/**
 * @author Bruno Salmon
 */
public final class Elemental2FastPixelReaderWriterProvider implements FastPixelReaderWriterProvider {

    @Override
    public FastPixelReaderWriter createFastPixelReaderWriter(Image image) {
        return new Elemental2FastPixelReaderWriter(image);
    }
}
