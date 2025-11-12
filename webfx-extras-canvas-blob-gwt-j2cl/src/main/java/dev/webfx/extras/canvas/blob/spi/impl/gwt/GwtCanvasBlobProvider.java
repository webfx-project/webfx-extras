package dev.webfx.extras.canvas.blob.spi.impl.gwt;

import dev.webfx.extras.canvas.blob.spi.CanvasBlobProvider;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwtj2cl.shared.HtmlSvgNodePeer;
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
    public Future<Blob> createCanvasBlob(Canvas canvas) {
        return createCanvasBlob(canvas, "image/png"); // Default to PNG
    }

    @Override
    public Future<Blob> createCanvasBlob(Canvas canvas, String mimeType) {
        Promise<Blob> promise = Promise.promise();
        HtmlSvgNodePeer nodePeer = (HtmlSvgNodePeer) canvas.getOrCreateAndBindNodePeer();
        HTMLCanvasElement canvasElement = (HTMLCanvasElement) nodePeer.getElement();
        // Use the HTML5 Canvas toBlob API with specified MIME type
        canvasElement.toBlob(blob -> {
            promise.complete(Blob.create(blob));
            return null;
        }, mimeType); // Specify format: "image/png", "image/jpeg", "image/webp"
        return promise.future();
    }
}
