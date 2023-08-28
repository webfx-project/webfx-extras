package dev.webfx.extras.canvas.blob.spi.impl.openjfx;

import dev.webfx.extras.canvas.blob.spi.CanvasBlobProvider;
import dev.webfx.platform.async.Future;
import dev.webfx.platform.file.Blob;

/**
 * @author Bruno Salmon
 */
public class OpenJFXCanvasBlobProvider implements CanvasBlobProvider {

    @Override
    public Future<Blob> createCanvasBlob(javafx.scene.canvas.Canvas canvas) {
        return Future.failedFuture("Not yet implemented in OpenJFX");
    }
}
