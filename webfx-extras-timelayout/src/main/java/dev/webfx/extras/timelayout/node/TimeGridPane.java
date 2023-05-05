package dev.webfx.extras.timelayout.node;

import dev.webfx.extras.timelayout.ChildPosition;
import dev.webfx.extras.timelayout.TimeLayout;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Bruno Salmon
 */
public class TimeGridPane<C, T> extends GridPane {

    private final TimeLayout<C, T> timeLayout;
    private final ChildNodeGetter<C> childNodeGetter;

    public TimeGridPane(TimeLayout<C, T> timeLayout, ChildNodeGetter<C> childNodeGetter) {
        this.timeLayout = timeLayout;
        this.childNodeGetter = childNodeGetter;
        syncChildren();
        timeLayout.getChildren().addListener((ListChangeListener<C>) c -> syncChildren());
    }

    private void syncChildren() {
        getChildren().setAll(timeLayout.getChildren().stream().map(childNodeGetter::getNode).toArray(Node[]::new));
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
            ChildPosition childPosition = timeLayout.getChildPosition(i);
            int columnIndex = childPosition.getColumnIndex();
            int rowIndex = childPosition.getRowIndex();
            GridPane.setConstraints(child, columnIndex, rowIndex);
            maxColumnIndex = Math.max(maxColumnIndex, columnIndex);
        }
        getColumnConstraints().setAll(IntStream.range(0, maxColumnIndex + 1).mapToObj(i -> columnConstraints).collect(Collectors.toList()));
        super.layoutChildren();
    }

}
