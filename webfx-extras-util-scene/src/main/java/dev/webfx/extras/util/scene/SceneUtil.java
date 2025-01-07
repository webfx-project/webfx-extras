package dev.webfx.extras.util.scene;

import dev.webfx.extras.util.animation.Animations;
import dev.webfx.extras.util.control.ControlUtil;
import dev.webfx.kit.launcher.WebFxKitLauncher;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.Unregisterable;
import dev.webfx.kit.util.properties.UnregisterableListener;
import dev.webfx.platform.scheduler.Scheduled;
import dev.webfx.platform.uischeduler.AnimationFramePass;
import dev.webfx.platform.uischeduler.UiScheduler;
import dev.webfx.platform.util.Booleans;
import dev.webfx.platform.util.tuples.Pair;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Window;

import java.util.function.Consumer;

/**
 * @author Bruno Salmon
 */
public final class SceneUtil {

    public static void onSceneReady(Node node, Consumer<Scene> sceneConsumer) {
        if (node != null)
            onSceneReady(node.sceneProperty(), sceneConsumer);
    }

    public static void onSceneReady(Window window, Consumer<Scene> sceneConsumer) {
        if (window != null)
            onSceneReady(window.sceneProperty(), sceneConsumer);
    }

    public static void onSceneReady(ObservableValue<Scene> sceneProperty, Consumer<Scene> sceneConsumer) {
        FXProperties.onPropertySet(sceneProperty, sceneConsumer);
    }

    public static void onPrimarySceneReady(Consumer<Scene> sceneConsumer) {
        WebFxKitLauncher.onReady(() -> onSceneReady(WebFxKitLauncher.getPrimaryStage(), sceneConsumer));
    }

    public static Runnable getDefaultAccelerator(Scene scene) {
        return getAccelerator(scene, KeyCode.ENTER);
    }

    public static void setDefaultAccelerator(Scene scene, Runnable defaultAccelerator) {
        setAccelerator(scene, KeyCode.ENTER, defaultAccelerator);
    }

    public static Runnable getCancelAccelerator(Scene scene) {
        return getAccelerator(scene, KeyCode.ESCAPE);
    }

    public static void setCancelAccelerator(Scene scene, Runnable cancelAccelerator) {
        setAccelerator(scene, KeyCode.ESCAPE, cancelAccelerator);
    }

    public static Pair<Runnable, Runnable> getDefaultAndCancelAccelerators(Scene scene) {
        return new Pair(getDefaultAccelerator(scene), getCancelAccelerator(scene));
    }

    public static void setDefaultAndCancelAccelerators(Scene scene, Pair<Runnable, Runnable> accelerators) {
        setDefaultAccelerator(scene, accelerators.get1());
        setCancelAccelerator(scene, accelerators.get2());
    }

    private static Runnable getAccelerator(Scene scene, KeyCode keyCode) {
        KeyCodeCombination acceleratorKeyCodeCombination = new KeyCodeCombination(keyCode);
        return scene.getAccelerators().get(acceleratorKeyCodeCombination);
    }

    private static void setAccelerator(Scene scene, KeyCode keyCode, Runnable accelerator) {
        KeyCodeCombination acceleratorKeyCodeCombination = new KeyCodeCombination(keyCode);
        scene.getAccelerators().put(acceleratorKeyCodeCombination, accelerator);
    }

    public static Unregisterable runOnceFocusIsInside(Node node, boolean includesNullFocus, Runnable runnable) {
        return runOnceFocusIsInsideOrOutside(node, includesNullFocus, runnable, true);
    }

    public static Unregisterable runOnceFocusIsOutside(Node node, boolean includesNullFocus, Runnable runnable) {
        return runOnceFocusIsInsideOrOutside(node, includesNullFocus, runnable, false);
    }

    public static Unregisterable runOnceFocusIsInsideOrOutside(Node node, boolean includesNullFocus, Runnable runnable, boolean inside) {
        Property<Node> localFocusOwnerProperty;
        ObservableValue<Node> focusOwnerProperty;
        Unregisterable[] unregisterable = { null };
        boolean[] localFocusOwnerPropertyInitialised = { false };
        if (node.getScene() != null) {
            focusOwnerProperty = node.getScene().focusOwnerProperty();
            localFocusOwnerProperty = null;
        } else {
            focusOwnerProperty = localFocusOwnerProperty = new SimpleObjectProperty<>();
            onSceneReady(node, scene -> {
                localFocusOwnerPropertyInitialised[0] = true;
                localFocusOwnerProperty.bind(scene.focusOwnerProperty());
            });
        }
        unregisterable[0] = new UnregisterableListener(p -> {
            // When using localFocusOwnerProperty, we ignore the initial null value
            if (localFocusOwnerProperty != null && !localFocusOwnerPropertyInitialised[0]) {
                return;
            }
            Node newFocusOwner = (Node) p.getValue();
            if ((newFocusOwner == null ? includesNullFocus : inside == isFocusInsideNode(newFocusOwner, node))) {
                runnable.run();
                unregisterable[0].unregister();
            }
        }, focusOwnerProperty) {
            @Override
            public void unregister() {
                if (localFocusOwnerProperty != null)
                    localFocusOwnerProperty.unbind();
                super.unregister();
            }
        };
        return unregisterable[0];
    }

    public static boolean isFocusInsideNode(Node node) {
        Scene scene = node == null ? null : node.getScene();
        return scene != null && isFocusInsideNode(scene.getFocusOwner(), node);
    }

    private static boolean isFocusInsideNode(Node focusOwner, Node node) {
        return hasAncestor(focusOwner, node);
    }

    public static boolean hasAncestor(Node node, Node parent) {
        while (true) {
            if (node == parent)
                return true;
            if (node == null)
                return false;
            node = node.getParent();
        }
    }

    public static void autoFocusIfEnabled(Node node) {
        onSceneReady(node, scene -> {
            if (isAutoFocusEnabled(scene))
                node.requestFocus();
        });
    }

    public static boolean isAutoFocusEnabled(Scene scene) {
        // TODO: make it a user setting that can be stored in the device
        // Default behavior is to disable auto focus if this can cause a (probably unwanted) virtual keyboard to appear
        return isVirtualKeyboardShowing(scene) || !willAVirtualKeyboardAppearOnFocus(scene);
    }

    public static boolean willAVirtualKeyboardAppearOnFocus(Scene scene) {
        Boolean virtualKeyboardDetected = getSceneInfo(scene).virtualKeyboardDetected;
        if (virtualKeyboardDetected != null)
            return virtualKeyboardDetected;
        // No API for this so temporary implementation based on scene size
        return Math.min(scene.getWidth(), scene.getHeight()) < 800;
    }

    public static void installSceneFocusOwnerAutoScroll(Scene scene) {
        FXProperties.runOnPropertyChange(newFocusOwner -> {
            if (newFocusOwner instanceof TextInputControl) {
                scrollNodeToBeVerticallyVisibleOnScene(newFocusOwner, true, true);
                getSceneInfo(scene).touchTextInputFocusTime();
            }
        }, scene.focusOwnerProperty());
    }

    public static void installPrimarySceneFocusOwnerAutoScroll() {
        onPrimarySceneReady(SceneUtil::installSceneFocusOwnerAutoScroll);
    }

    public static boolean isVirtualKeyboardShowing(Scene scene) {
        return getSceneInfo(scene).isVirtualKeyboardShowing();
    }

    public static Unregisterable onVirtualKeyboardShowing(Scene scene, Runnable runnable) {
        return FXProperties.runOnPropertiesChange(showing -> {
            if (Booleans.isTrue(showing))
                runnable.run();
        }, getSceneInfo(scene).virtualKeyboardShowingProperty);
    }


    public static boolean isNodeVerticallyVisibleOnScene(Node node) {
        Bounds layoutBounds = node.getLayoutBounds();
        double minY = node.localToScene(0, layoutBounds.getMinY()).getY();
        double maxY = node.localToScene(0, layoutBounds.getMaxY()).getY();
        Scene scene = node.getScene();
        return minY >= 0 && maxY <= scene.getHeight();
    }

    public static boolean scrollNodeToBeVerticallyVisibleOnScene(Node node) {
        // Note: onlyIfNotVisible = false was creating unwanted scroll on the login/guest window
        return scrollNodeToBeVerticallyVisibleOnScene(node, true, true);
    }

    public static boolean scrollNodeToBeVerticallyVisibleOnScene(Node node, boolean onlyIfNotVisible, boolean animate) {
        ScrollPane scrollPane = ControlUtil.findScrollPaneAncestor(node);
        if (scrollPane != null && (!onlyIfNotVisible || !isNodeVerticallyVisibleOnScene(node))) {
            double vValue = ControlUtil.computeVerticalScrollNodeWishedValue(node);
            Object timeline = scrollPane.getProperties().get("timeline");
            if (timeline instanceof Timeline)
                ((Timeline) timeline).stop();
            scrollPane.getProperties().put("timeline", Animations.animateProperty(scrollPane.vvalueProperty(), vValue, animate));
            return true;
        }
        return false;
    }


    private static SceneInfo getSceneInfo(Scene scene) {
        ObservableMap<Object, Object> properties = scene.getProperties();
        Object p = properties.get("sceneInfo");
        if (!(p instanceof SceneInfo))
            properties.put("sceneInfo", p = new SceneInfo(scene));
        return (SceneInfo) p;
    }

    private final static long MAX_DELAY_MILLIS_BETWEEN_FOCUS_AND_VIRTUAL_KEYBOARD = 1000;

    private static final class SceneInfo {
        private Boolean virtualKeyboardDetected; // null = don't know yet, true = yes we detected it, no = we detected it was not here
        private final BooleanProperty virtualKeyboardShowingProperty = new SimpleBooleanProperty();
        private long lastTextInputFocusTime;
        private Scheduled noVirtualKeyboardDetectionScheduled;

        SceneInfo(Scene scene) {
            FXProperties.runOnDoublePropertyChange(newHeight -> {
                boolean showing = lastTextInputFocusTime > 0 && System.currentTimeMillis() < lastTextInputFocusTime + MAX_DELAY_MILLIS_BETWEEN_FOCUS_AND_VIRTUAL_KEYBOARD;
                if (showing) {
                    cancelLastNoVirtualKeyboardDetection();
                    virtualKeyboardDetected = true;
                }
                virtualKeyboardShowingProperty.setValue(showing);
                // Also keeping focused text input control visible on screen when changing height
                UiScheduler.scheduleInAnimationFrame(() -> {
                    Node focusOwner = scene.getFocusOwner();
                    if (focusOwner instanceof TextInputControl)
                        scrollNodeToBeVerticallyVisibleOnScene(focusOwner, true, true);
                }, 2, AnimationFramePass.SCENE_PULSE_LAYOUT_PASS);
            }, scene.heightProperty());
        }

        void touchTextInputFocusTime() {
            lastTextInputFocusTime = System.currentTimeMillis();
            cancelLastNoVirtualKeyboardDetection();
            if (!isVirtualKeyboardShowing())
                noVirtualKeyboardDetectionScheduled = UiScheduler.scheduleDelay(MAX_DELAY_MILLIS_BETWEEN_FOCUS_AND_VIRTUAL_KEYBOARD, () -> virtualKeyboardDetected = false);
        }

        public boolean isVirtualKeyboardShowing() {
            return virtualKeyboardShowingProperty.get();
        }

        void cancelLastNoVirtualKeyboardDetection() {
            if (noVirtualKeyboardDetectionScheduled != null)
                noVirtualKeyboardDetectionScheduled.cancel();
            noVirtualKeyboardDetectionScheduled = null;
        }
    }
}
