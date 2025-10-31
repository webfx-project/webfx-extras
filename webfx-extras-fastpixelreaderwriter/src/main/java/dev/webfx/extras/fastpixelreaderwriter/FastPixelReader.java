package dev.webfx.extras.fastpixelreaderwriter;

import javafx.scene.image.Image;

/**
 * @author Bruno Salmon
 */
public interface FastPixelReader {

    Image getImage();

    void goToPixel(int x, int y);

    boolean gotToNextPixel();

    int getRed();

    int getGreen();

    int getBlue();

    int getOpacity();

}
