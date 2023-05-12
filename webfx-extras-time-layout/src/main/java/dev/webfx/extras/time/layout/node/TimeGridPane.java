package dev.webfx.extras.time.layout.node;

import dev.webfx.extras.time.layout.impl.LayoutBounds;
import dev.webfx.extras.time.layout.TimeLayout;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Bruno Salmon
 */
public class TimeGridPane<C, T> extends GridPane {

    private final TimeLayout<C, T> timeLayout;
    private final Function<C, Node> childNodeGetter;

    public TimeGridPane(TimeLayout<C, T> timeLayout, Function<C, Node> childNodeGetter) {
        this.timeLayout = timeLayout;
        this.childNodeGetter = childNodeGetter;
        syncChildren();
        timeLayout.getChildren().addListener((ListChangeListener<C>) c -> syncChildren());
    }

    private void syncChildren() {
        getChildren().setAll(timeLayout.getChildren().stream().map(childNodeGetter).toArray(Node[]::new));
    }

    @Override
    protected void layoutChildren() {
        timeLayout.layout(getWidth(), getHeight());
        ObservableList<Node> children = getChildren();
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        int maxColumnIndex = 0;
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            LayoutBounds<C, T> cp = timeLayout.getChildPosition(i);
            int columnIndex = cp.getColumnIndex();
            int rowIndex = cp.getRowIndex();
            GridPane.setConstraints(child, columnIndex, rowIndex);
            maxColumnIndex = Math.max(maxColumnIndex, columnIndex);
        }
        getColumnConstraints().setAll(IntStream.range(0, maxColumnIndex + 1).mapToObj(i -> columnConstraints).collect(Collectors.toList()));
        super.layoutChildren();
    }

}
