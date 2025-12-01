package dev.webfx.extras.time.layout.gantt;

import dev.webfx.extras.geometry.Bounds;
import dev.webfx.extras.time.layout.TimeLayout;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;

import java.time.temporal.Temporal;
import java.util.function.Function;
import java.util.stream.Stream;

public interface GanttLayout<C, T extends Temporal> extends TimeLayout<C, T> {

    GanttLayout<C, T> setChildParentReader(Function<C, ?> childParentReader);

    GanttLayout<C, T> setChildGrandparentReader(Function<C, ?> childGrandparentReader);

    <P> GanttLayout<C, T> setParentGrandparentReader(Function<P, ?> parentGrandparentReader);

    GanttLayout<C, T> setChildTetrisMinWidthReader(Function<C, Double> childTetrisMinWidthReader);

    GanttLayout<C, T> setChildYPositionGetter(Function<C, Double> childYPositionGetter);

    GanttLayout<C, T> setGrandparentHeaderPosition(HeaderPosition headerPosition);

    GanttLayout<C, T> setParentHeaderPosition(HeaderPosition headerPosition);

    GanttLayout<C, T> setParentHeaderWidth(double parentHeaderWidth);

    double getParentHeaderWidth();

    GanttLayout<C, T> setGrandparentHeaderHeight(double grandparentHeaderHeight);

    double getGrandparentHeaderHeight();

    GanttLayout<C, T> setTetrisPacking(boolean tetrisPacking);

    boolean isTetrisPacking();

    int getRowIndexInParentRow(C child);

    int getRowIndexInParentRow(Bounds cb);

    Stream<C> streamChildrenInParentRowAtRowIndex(Object parent, int rowIndex);

    GanttLayout<C, T> setParentRowCollapseEnabled(boolean parentRowCollapseEnabled);

    boolean isParentRowInitiallyCollapsed();

    GanttLayout<C, T> setParentRowInitiallyCollapsed(boolean parentRowInitiallyCollapsed);

    boolean isParentRowCollapseEnabled();

    Bounds getParentRowCollapseChevronLocalBounds();

    BooleanProperty parentsProvidedProperty();

    GanttLayout<C, T> setParentsProvided(boolean parentsProvided);

    boolean isParentsProvided();

    <P> ObservableList<P> getParents();


}
