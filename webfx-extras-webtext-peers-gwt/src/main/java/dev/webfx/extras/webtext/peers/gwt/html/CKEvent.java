package dev.webfx.extras.webtext.peers.gwt.html;

import elemental2.dom.Event;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * @author Bruno Salmon
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
final class CKEvent extends Event {

    public CKEvent(String type) {
        super(type);
    }

    public CKEditor editor;
}
