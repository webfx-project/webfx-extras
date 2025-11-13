package dev.webfx.extras.canvas.blob.spi.impl.gwt;

import dev.webfx.extras.canvas.blob.spi.CanvasBlobProvider;
import dev.webfx.platform.async.Future;
import dev.webfx.platform.async.Promise;
import dev.webfx.platform.blob.Blob;
import elemental2.dom.HTMLCanvasElement;
import javafx.scene.canvas.Canvas;

/**
 * @author Bruno Salmon
 */
public class GwtCanvasBlobProvider implements CanvasBlobProvider {

    @Override
    public Future<Blob> createCanvasBlob(Canvas canvas, String mimeType) {  // Specify format: "image/png", "image/jpeg", "image/webp"
        if (canvas.getOrCreateAndBindNodePeer() instanceof HTMLCanvasElement canvasElement) {
            Promise<Blob> promise = Promise.promise();
            // Use the HTML5 Canvas toBlob API with the specified MIME type
            canvasElement.toBlob(blob -> {
                promise.complete(Blob.create(blob));
                return null;
            }, mimeType);
            return promise.future();
        }
        return Future.failedFuture("HTML Canvas was not found");
    }
}
