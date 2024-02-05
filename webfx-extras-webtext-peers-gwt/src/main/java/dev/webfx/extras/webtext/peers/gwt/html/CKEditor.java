package dev.webfx.extras.webtext.peers.gwt.html;

import elemental2.dom.Element;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * @author Bruno Salmon
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
final class CKEditor {

    public Element container;

    public native void resize(double width, double height);

    public native void on(String type, OnFn listener);
    public native void setData(String data);
    public native String getData();
    public native void destroy();

    @JsMethod(namespace = "CKEDITOR")
    public native static CKEditor replace(Element element, JsPropertyMap options);

    @JsFunction
    public interface InstanceReadyFn {
        void onInvoke(CKEvent e);
    }

    @JsFunction
    public interface OnFn {
        void onInvoke(CKEvent e);
    }
}
