package dev.webfx.extras.layer;

import dev.webfx.extras.geometry.HasVisibleProperty;
import javafx.collections.ObservableList;

public interface Layer<T> extends HasVisibleProperty {

    ObservableList<T> getChildren();

}
