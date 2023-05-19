package dev.webfx.extras.time.layout.gantt;

import dev.webfx.extras.time.layout.TimeLayout;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;

import java.time.temporal.Temporal;
import java.util.function.Function;

public interface GanttLayout<C, T extends Temporal> extends TimeLayout<C, T> {

    GanttLayout<C, T> setChildParentReader(Function<C, ?> childParentReader);

    GanttLayout<C, T> setChildGrandparentReader(Function<C, ?> childGrandparentReader);

    <P> GanttLayout<C, T> setParentGrandparentReader(Function<P, ?> parentGrandparentReader);

    GanttLayout<C, T> setChildTetrisMinWidthReader(Function<C, Double> childTetrisMinWidthReader);

    GanttLayout<C, T> setGrandparentHeaderPosition(HeaderPosition headerPosition);

    GanttLayout<C, T> setParentWidth(double parentWidth);

    double getParentWidth();

    GanttLayout<C, T> setGrandparentHeaderHeight(double grandparentHeaderHeight);

    double getGrandparentHeaderHeight();

    GanttLayout<C, T> setTetrisPacking(boolean tetrisPacking);

    boolean isTetrisPacking();

    BooleanProperty parentsProvidedProperty();

    GanttLayout<C, T> setParentsProvided(boolean parentsProvided);

    boolean isParentsProvided();

    <P> ObservableList<P> getParents();


}
