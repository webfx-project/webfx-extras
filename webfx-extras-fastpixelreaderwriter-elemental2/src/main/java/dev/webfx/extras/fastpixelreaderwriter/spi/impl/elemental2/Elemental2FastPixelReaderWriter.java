package dev.webfx.extras.fastpixelreaderwriter.spi.impl.elemental2;

import dev.webfx.extras.fastpixelreaderwriter.FastPixelReaderWriter;
import dev.webfx.kit.mapper.peers.javafxgraphics.elemental2.html.ImageDataHelper;
import elemental2.core.Uint8ClampedArray;
import elemental2.dom.ImageData;
import javafx.scene.image.Image;

/**
 * @author Bruno Salmon
 */
public final class Elemental2FastPixelReaderWriter implements FastPixelReaderWriter {

    private static final int RED_OFFSET   = 0;
    private static final int GREEN_OFFSET = 1;
    private static final int BLUE_OFFSET  = 2;
    private static final int ALPHA_OFFSET = 3;

    private final Image image;
    private final int width;
    private final Uint8ClampedArray data;
    private final int maxIndex;
    private int index = -4;

    public Elemental2FastPixelReaderWriter(Image image) {
        this.image = image;
        width = (int) image.getWidth();
        ImageData imageData = ImageDataHelper.getOrCreateImageDataAssociatedWithImage(image);
        data = imageData.data;
        maxIndex = imageData.data.length - 4;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public void goToPixel(int x, int y) {
        index = 4 * (y * width + x);
    }

    @Override
    public boolean gotToNextPixel() {
        if (index >= maxIndex)
            return false;
        index += 4;
        return true;
    }

    @Override
    public int getRed() {
        return data.getAt(index + RED_OFFSET).intValue();
    }

    @Override
    public int getGreen() {
        return data.getAt(index + GREEN_OFFSET).intValue();
    }

    @Override
    public int getBlue() {
        return data.getAt(index + BLUE_OFFSET).intValue();
    }

    @Override
    public int getOpacity() {
        return data.getAt(index + ALPHA_OFFSET).intValue();
    }

    @Override
    public void setRed(int red) {
        data.setAt(index + RED_OFFSET, (double) red);
    }

    @Override
    public void setGreen(int green) {
        data.setAt(index + GREEN_OFFSET, (double) green);
    }

    @Override
    public void setBlue(int blue) {
        data.setAt(index + BLUE_OFFSET, (double) blue);
    }

    @Override
    public void setOpacity(int opacity) {
        data.setAt(index + ALPHA_OFFSET, (double) opacity);
    }

    @Override
    public void writeCache() {
        image.setPeerCanvasDirty(true);
    }
}
