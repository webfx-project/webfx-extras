package dev.webfx.extras.canvas.blob.spi.impl.openjfx;

import dev.webfx.extras.canvas.blob.spi.CanvasBlobProvider;
import dev.webfx.platform.async.Future;
import dev.webfx.platform.blob.Blob;
import dev.webfx.platform.file.spi.impl.java.JavaFile;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * @author Bruno Salmon
 */
public class OpenJFXCanvasBlobProvider implements CanvasBlobProvider {

    @Override
    public Future<Blob> createCanvasBlob(javafx.scene.canvas.Canvas canvas) {
        WritableImage snapshot = canvas.snapshot(null, null);
        // Save the image to a file
        try {
            File file = File.createTempFile("webfx-export-canvas", ".png");
            file.deleteOnExit();
            ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, null), "png", file);
            return Future.succeededFuture(new JavaFile(file));
        } catch (IOException e) {
            return Future.failedFuture(e);
        }
    }
}
