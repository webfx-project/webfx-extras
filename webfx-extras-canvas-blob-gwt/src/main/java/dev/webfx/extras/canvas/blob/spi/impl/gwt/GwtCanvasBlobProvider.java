package dev.webfx.extras.canvas.blob.spi.impl.gwt;

import dev.webfx.extras.canvas.blob.spi.CanvasBlobProvider;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.shared.HtmlSvgNodePeer;
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
        Promise<Blob> promise = Promise.promise();
        HtmlSvgNodePeer nodePeer = (HtmlSvgNodePeer) canvas.getOrCreateAndBindNodePeer();
        HTMLCanvasElement canvasElement = (HTMLCanvasElement) nodePeer.getElement();
        canvasElement.toBlob(blob -> {
            promise.complete(Blob.create(blob));
            return null;
        });
        return promise.future();
    }
}
