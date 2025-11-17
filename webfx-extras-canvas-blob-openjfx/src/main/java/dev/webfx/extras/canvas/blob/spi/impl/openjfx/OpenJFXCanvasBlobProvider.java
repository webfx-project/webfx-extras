package dev.webfx.extras.canvas.blob.spi.impl.openjfx;

import dev.webfx.extras.canvas.blob.spi.CanvasBlobProvider;
import dev.webfx.platform.async.Future;
import dev.webfx.platform.blob.Blob;
import dev.webfx.platform.file.spi.impl.jre.JreFile;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * @author Bruno Salmon
 */
public class OpenJFXCanvasBlobProvider implements CanvasBlobProvider {

    @Override
    public Future<Blob> createCanvasBlob(javafx.scene.canvas.Canvas canvas, String mimeType) {
        WritableImage snapshot = canvas.snapshot(null, null);
        // Convert MIME type to ImageIO format name
        String format = mimeTypeToFormat(mimeType);
        String extension = mimeTypeToExtension(mimeType);

        try {
            File file = File.createTempFile("webfx-export-canvas", extension);
            file.deleteOnExit();
            ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, null), format, file);
            return Future.succeededFuture(new JreFile(file));
        } catch (IOException e) {
            return Future.failedFuture(e);
        }
    }

    private String mimeTypeToFormat(String mimeType) {
        if (mimeType == null)
            return "png";
        return switch (mimeType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/bmp" -> "bmp";
            default -> "png";
        };
    }

    private String mimeTypeToExtension(String mimeType) {
        return "." + mimeTypeToFormat(mimeType);
    }
}
