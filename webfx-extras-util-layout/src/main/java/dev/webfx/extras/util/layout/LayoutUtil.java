package dev.webfx.extras.util.layout;

import dev.webfx.extras.util.background.BackgroundFactory;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.util.Arrays;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import static javafx.scene.layout.Region.USE_PREF_SIZE;

/**
 * @author Bruno Salmon
 */
public final class LayoutUtil {

    private LayoutUtil() {}

    public static GridPane createGoldLayout(Region child) {
        return createGoldLayout(child, 0, 0);
    }

    public static GridPane createGoldLayout(Region child, double percentageWidth, double percentageHeight) {
        return createGoldLayout(child, percentageWidth, percentageHeight, BackgroundFactory.newBackground(Color.gray(0.3, 0.5)));
    }

    public static GridPane createGoldLayout(Region child, double percentageWidth, double percentageHeight, Background background) {
        GridPane goldPane = new GridPane();
        goldPane.setAlignment(Pos.TOP_CENTER); // Horizontal alignment
        RowConstraints headerRowConstraints = new RowConstraints();
        // Making the gold pane invisible during a few animation frames because its height may not be stable on start
        // Temporary commented as scheduleInAnimationFrame() doesn't work with the FxUiSchedulerProvider TODO Fix that
        // goldPane.setVisible(false);
        // UiScheduler.scheduleInAnimationFrame(() -> goldPane.setVisible(true), 5, AnimationFramePass.SCENE_PULSE_LAYOUT_PASS);
        headerRowConstraints.prefHeightProperty().bind(FXProperties.combine(goldPane.heightProperty(), child.heightProperty(),
                (gpHeight, cHeight) -> {
                    if (percentageHeight != 0)
                        child.setPrefHeight(gpHeight.doubleValue() * percentageHeight);
                    Platform.runLater(() -> goldPane.getRowConstraints().setAll(headerRowConstraints));
                    return (gpHeight.doubleValue() - cHeight.doubleValue()) / 2.61;
                }));
        if (percentageWidth != 0)
            child.prefWidthProperty().bind(FXProperties.compute(goldPane.widthProperty(), gpWidth -> gpWidth.doubleValue() * percentageWidth));
        goldPane.add(setMaxSizeToPref(child), 0, 1);
        if (background != null)
            goldPane.setBackground(background);
        // For any strange reason this additional code is required for the gwt version to work properly (unnecessary for the JavaFX version)
        goldPane.widthProperty().addListener(observable -> goldPane.requestLayout());
        return goldPane;
    }

    public static Region createHSpace(double width) {
        return setMinWidth(new Region(), width);
    }

    public static Region createHGrowable() {
        return setHGrowable(setMaxWidthToInfinite(new Region()));
    }

    public static <N extends Node> N setHGrowable(N node) {
        HBox.setHgrow(node, Priority.ALWAYS);
        return node;
    }

    public static <N extends Node> N setVGrowable(N node) {
        VBox.setVgrow(node, Priority.ALWAYS);
        return node;
    }

    public static <N extends Region> N setMinSizeToZero(N region) {
        return setMinSize(region, 0);
    }

    public static <N extends Region> N setMinSize(N region, double value) {
        return setMinSize(region, value, value);
    }

    public static <N extends Region> N setMinSize(N region, double minWidth, double minHeight) {
        region.setMinWidth(minWidth);
        region.setMinHeight(minHeight);
        return region;
    }

    public static <N extends Region> N setMinSizeToZeroAndPrefSizeToInfinite(N region) {
        return setMinSizeToZero(setMaxPrefSizeToInfinite(region));
    }

    public static <N extends Region> N setMaxPrefSizeToInfinite(N region) {
        return setMaxPrefSize(region, Double.MAX_VALUE);
    }

    public static <N extends Region> N setMaxPrefSize(N region, double value) {
        return setPrefSize(setMaxSize(region, value), value);
    }

    public static <N extends Region> N setPrefSize(N region, double value) {
        region.setPrefWidth(value);
        region.setPrefHeight(value);
        return region;
    }

    public static <N extends Region> N setMaxSizeToInfinite(N region) {
        return setMaxSize(region, Double.MAX_VALUE);
    }

    public static <N extends Region> N setMaxSizeToPref(N region) {
        return setMaxSize(region, USE_PREF_SIZE);
    }

    private static <N extends Region> N setMaxSize(N region, double value) {
        region.setMaxWidth(value);
        region.setMaxHeight(value);
        return region;
    }

    public static <N extends Region> N setPrefWidthToInfinite(N region) {
        region.setPrefWidth(Double.MAX_VALUE);
        return setMaxWidthToInfinite(region);
    }

    public static <N extends Region> N setPrefHeightToInfinite(N region) {
        region.setPrefHeight(Double.MAX_VALUE);
        return setMaxHeightToInfinite(region);
    }

    public static <N extends Region> N setMaxWidthToInfinite(N region) {
        region.setMaxWidth(Double.MAX_VALUE);
        return region;
    }

    public static <N extends Region> N setMaxHeightToInfinite(N region) {
        region.setMaxHeight(Double.MAX_VALUE);
        return region;
    }

    public static <N extends Region> N setMinMaxWidthToPref(N region) {
        setMinWidthToPref(region);
        setMaxWidthToPref(region);
        return region;
    }

    public static <N extends Region> N setMinWidthToPref(N region) {
        return setMinWidth(region, USE_PREF_SIZE);
    }

    public static <N extends Region> N setMaxWidthToPref(N region) {
        return setMaxWidth(region, USE_PREF_SIZE);
    }


    public static <N extends Region> N setMinWidth(N region, double value) {
        region.setMinWidth(value);
        return region;
    }

    public static <N extends Region> N setMaxWidth(N region, double value) {
        region.setMaxWidth(value);
        return region;
    }

    public static <N extends Region> N setMinMaxHeightToPref(N region) {
        setMinHeightToPref(region);
        setMaxHeightToPref(region);
        return region;
    }

    public static <N extends Region> N setMinHeightToPref(N region) {
        return setMinHeight(region, USE_PREF_SIZE);
    }

    public static <N extends Region> N setMaxHeightToPref(N region) {
        return setMaxHeight(region, USE_PREF_SIZE);
    }

    public static <N extends Region> N setMinHeight(N region, double value) {
        region.setMinHeight(value);
        return region;
    }

    public static <N extends Region> N setMaxHeight(N region, double value) {
        region.setMaxHeight(value);
        return region;
    }

    public static <N extends Node> N setUnmanagedWhenInvisible(N node) {
        node.managedProperty().bind(node.visibleProperty());
        return node;
    }

    public static <N extends Node> N setUnmanagedWhenInvisible(N node, ObservableValue<Boolean> visibleProperty) {
        node.visibleProperty().bind(visibleProperty);
        return setUnmanagedWhenInvisible(node);
    }

    public static void setAllUnmanagedWhenInvisible(Node... nodes) {
        Arrays.forEach(nodes, LayoutUtil::setUnmanagedWhenInvisible);
    }

    public static void setAllUnmanagedWhenInvisible(boolean initialVisibility, Node... nodes) {
        Arrays.forEach(nodes, node -> node.setVisible(initialVisibility));
        setAllUnmanagedWhenInvisible(nodes);
    }

    public static <N extends Region> N setPadding(N content, double top, double right, double bottom, double left) {
        content.setPadding(new Insets(top, right, bottom, left));
        return content;
    }

    public static <N extends Region> N setPadding(N content, double topBottom, double rightLeft) {
        return setPadding(content, topBottom, rightLeft, topBottom, rightLeft);
    }

    public static <N extends Region> N setPadding(N content, double topRightBottomLeft) {
        return setPadding(content, new Insets(topRightBottomLeft));
    }

    public static <N extends Region> N setPadding(N content, Insets padding) {
        content.setPadding(padding);
        return content;
    }

    public static <N extends Region> N createPadding(N content) {
        return setPadding(content, new Insets(10));
    }

    public static <N extends Region> N removePadding(N content) {
        return setPadding(content, Insets.EMPTY);
    }

    // lookup method

    // ScrollPane utility methods

    // Snap methods from Region (but public)

    /**
     * If snapToPixel is true, then the value is ceil'd using Math.ceil. Otherwise,
     * the value is simply returned.
     *
     * @param value The value that needs to be snapped
     * @param snapToPixel Whether to snap to pixel
     * @return value either as passed in or ceil'd based on snapToPixel
     */
    private static double snapSize(double value, boolean snapToPixel) {
        return snapToPixel ? Math.ceil(value) : value;
    }

    /**
     * Returns a value ceiled to the nearest pixel.
     * @param value the size value to be snapped
     * @return value ceiled to nearest pixel
     */
    public static double snapSize(double value) {
        return snapSize(value, true);
    }

    /**
     * If snapToPixel is true, then the value is rounded using Math.round. Otherwise,
     * the value is simply returned.
     *
     * @param value The value that needs to be snapped
     * @param snapToPixel Whether to snap to pixel
     * @return value either as passed in or rounded based on snapToPixel
     */
    public static double snapPosition(double value, boolean snapToPixel) {
        return snapToPixel ? Math.round(value) : value;
    }

    /**
     * Returns a value rounded to the nearest pixel.
     * @param value the position value to be snapped
     * @return value rounded to nearest pixel
     */
    public static double snapPosition(double value) {
        return snapPosition(value, true);
    }

    // used for layout to adjust widths to honor the min/max policies consistently
    public static double boundedSize(double value, double min, double max) {
        // if max < value, return max
        // if min > value, return min
        // if min > max, return min
        return Math.min(Math.max(value, min), Math.max(min,max));
    }
}
