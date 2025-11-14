package dev.webfx.extras.canvas.blob.spi;

import dev.webfx.platform.async.Future;
import dev.webfx.platform.blob.Blob;
import javafx.scene.canvas.Canvas;

/**
 * @author Bruno Salmon
 */
public interface CanvasBlobProvider {

    default Future<Blob> createCanvasBlob(Canvas canvas) {
        return createCanvasBlob(canvas, "image/png");
    }

    Future<Blob> createCanvasBlob(Canvas canvas, String mimeType);

}
