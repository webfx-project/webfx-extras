package dev.webfx.extras.flextable;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public final class FlexColumn {

    private final String columnId;
    private final DoubleProperty minWidthProperty = new SimpleDoubleProperty(0);
    private final DoubleProperty prefWidthProperty = new SimpleDoubleProperty(-1);
    private final DoubleProperty maxWidthProperty = new SimpleDoubleProperty(-1);
    private final DoubleProperty widthProperty = new SimpleDoubleProperty(-1);
    private final ObjectProperty<Node> headerNodeProperty = new SimpleObjectProperty<>();

    public FlexColumn(String columnId) {
        this.columnId = columnId;
    }

    public String getColumnId() {
        return columnId;
    }

    public double getMinWidth() {
        return minWidthProperty.get();
    }

    public void setMinWidth(double minWidth) {
        minWidthProperty.set(minWidth);
    }

    public DoubleProperty minWidthProperty() {
        return minWidthProperty;
    }

    public double getPrefWidth() {
        return prefWidthProperty.get();
    }

    public void setPrefWidth(double prefWidth) {
        prefWidthProperty.set(prefWidth);
    }

    public DoubleProperty prefWidthProperty() {
        return prefWidthProperty;
    }

    public double getMaxWidth() {
        return maxWidthProperty.get();
    }

    public void setMaxWidth(double maxWidth) {
        maxWidthProperty.set(maxWidth);
    }

    public DoubleProperty maxWidthProperty() {
        return maxWidthProperty;
    }

    public double getWidth() {
        return widthProperty.get();
    }

    public void setWidth(double width) {
        widthProperty.set(width);
    }

    public DoubleProperty widthProperty() {
        return widthProperty;
    }

    public Node getHeaderNode() {
        return headerNodeProperty.get();
    }

    public void setHeaderNode(Node headerNode) {
        headerNodeProperty.set(headerNode);
    }

    public ObjectProperty<Node> headerNodeProperty() {
        return headerNodeProperty;
    }
}
