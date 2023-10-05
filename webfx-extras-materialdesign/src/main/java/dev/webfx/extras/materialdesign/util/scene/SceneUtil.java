package dev.webfx.extras.materialdesign.util.scene;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import dev.webfx.kit.util.properties.FXProperties;

import java.util.function.Consumer;

/**
 * @author Bruno Salmon
 */
public final class SceneUtil {

    public static void onSceneReady(Node node, Consumer<Scene> sceneConsumer) {
        if (node != null)
            onSceneReady(node.sceneProperty(), sceneConsumer);
    }

    public static void onSceneReady(ObservableValue<Scene> sceneProperty, Consumer<Scene> sceneConsumer) {
        FXProperties.onPropertySet(sceneProperty, sceneConsumer);
    }

    public static boolean isFocusInside(Node node) {
        Scene scene = node == null ? null : node.getScene();
        return scene != null && isFocusInside(node, scene.getFocusOwner());
    }

    private static boolean isFocusInside(Node node, Node focusOwner) {
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

}
