package dev.webfx.extras.fastpixelreaderwriter.spi.impl.teavm;

import dev.webfx.extras.fastpixelreaderwriter.FastPixelReaderWriter;
import dev.webfx.kit.mapper.peers.javafxgraphics.elemental2.html.ImageDataHelper;
import javafx.scene.image.Image;
import org.teavm.jso.canvas.ImageData;

/**
 * @author Bruno Salmon
 */
public final class TeaVMFastPixelReaderWriter implements FastPixelReaderWriter {

    private final Image image;
    private final ImageData imageData;
    private final int maxIndex;
    private int index = -4;

    public TeaVMFastPixelReaderWriter(Image image) {
        this.image = image;
        // ImageDataHelper returns an elemental2.dom.ImageData, but it's basically a reference to the JS ImageData
        elemental2.dom.ImageData orCreateImageDataAssociatedWithImage = ImageDataHelper.getOrCreateImageDataAssociatedWithImage(image);
        // So we can cast it to the TeaVM JSO ImageData, because it's also basically a reference to the JS ImageData
        imageData = (ImageData) (Object) orCreateImageDataAssociatedWithImage;
        maxIndex = imageData.getData().getLength() - 4;
        image.setPeerCanvasDirty(true);
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public void goToPixel(int x, int y) {
        index = 4 * (y * imageData.getWidth() + x);
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
        return imageData.getData().get(index);
    }

    @Override
    public int getGreen() {
        return imageData.getData().get(index + 1);
    }

    @Override
    public int getBlue() {
        return imageData.getData().get(index + 2);
    }

    @Override
    public int getOpacity() {
        return imageData.getData().get(index + 3);
    }

    @Override
    public void setRed(int red) {
        imageData.getData().set(index, red);
        image.setPeerCanvasDirty(true);
    }

    @Override
    public void setGreen(int green) {
        imageData.getData().set(index + 1, green);
        image.setPeerCanvasDirty(true);
    }

    @Override
    public void setBlue(int blue) {
        imageData.getData().set(index + 2, blue);
        image.setPeerCanvasDirty(true);
    }

    @Override
    public void setOpacity(int opacity) {
        imageData.getData().set(index + 3, opacity);
        image.setPeerCanvasDirty(true);
    }
}
