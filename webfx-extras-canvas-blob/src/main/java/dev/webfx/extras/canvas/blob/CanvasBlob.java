package dev.webfx.extras.canvas.blob;

import dev.webfx.extras.canvas.blob.spi.CanvasBlobProvider;
import dev.webfx.platform.async.Future;
import dev.webfx.platform.blob.Blob;
import dev.webfx.platform.service.SingleServiceProvider;
import javafx.scene.canvas.Canvas;

import java.util.ServiceLoader;

/**
 * @author Bruno Salmon
 */
public final class CanvasBlob {

    private static CanvasBlobProvider getProvider() {
        return SingleServiceProvider.getProvider(CanvasBlobProvider.class, () -> ServiceLoader.load(CanvasBlobProvider.class));
    }

    public static Future<Blob> createCanvasBlob(Canvas canvas) {
        return getProvider().createCanvasBlob(canvas);
    }

}
