package dev.webfx.extras.canvas.blob.spi;

import dev.webfx.platform.async.Future;
import dev.webfx.platform.file.Blob;
import javafx.scene.canvas.Canvas;

/**
 * @author Bruno Salmon
 */
public interface CanvasBlobProvider {

    Future<Blob> createCanvasBlob(Canvas canvas);

}
