package dev.webfx.extras.canvas.blob.spi;

import dev.webfx.platform.async.Future;
import dev.webfx.platform.blob.Blob;
import javafx.scene.canvas.Canvas;

/**
 * @author Bruno Salmon
 */
public interface CanvasBlobProvider {

    Future<Blob> createCanvasBlob(Canvas canvas);

    default Future<Blob> createCanvasBlob(Canvas canvas, String mimeType) {
        // Default implementation ignores mimeType for backward compatibility
        return createCanvasBlob(canvas);
    }

}
