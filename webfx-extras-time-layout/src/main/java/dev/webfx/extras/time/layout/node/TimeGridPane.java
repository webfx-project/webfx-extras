package dev.webfx.extras.time.layout.node;

import dev.webfx.extras.time.layout.TimeLayout;
import dev.webfx.extras.time.layout.impl.ChildBounds;
import dev.webfx.kit.util.properties.ObservableLists;
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
        ObservableLists.runNowAndOnListChange(c -> syncChildren(), timeLayout.getChildren());
    }

    private void syncChildren() {
        ObservableList<C> timeLayoutChildren = timeLayout.getChildren();
        getChildren().clear();
        for (int i = 0; i < timeLayoutChildren.size(); i++) {
            Node child = childNodeGetter.apply(timeLayoutChildren.get(i));
            ChildBounds<C, T> cb = timeLayout.getChildBounds(i);
            add(child, cb.getColumnIndex(), cb.getRowIndex());
        }
    }

    @Override
    protected void layoutChildren() {
        timeLayout.layout(getWidth(), getHeight());
        ObservableList<Node> children = getChildren();
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        int maxColumnIndex = 0;
        for (int i = 0; i < children.size(); i++) {
            ChildBounds<C, T> cb = timeLayout.getChildBounds(i);
            maxColumnIndex = Math.max(maxColumnIndex, cb.getColumnIndex());
        }
        getColumnConstraints().setAll(IntStream.range(0, maxColumnIndex + 1).mapToObj(i -> columnConstraints).collect(Collectors.toList()));
        super.layoutChildren();
    }

}
