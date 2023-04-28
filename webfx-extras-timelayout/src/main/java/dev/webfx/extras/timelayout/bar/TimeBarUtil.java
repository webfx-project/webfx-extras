package dev.webfx.extras.timelayout.bar;

import dev.webfx.extras.timelayout.TimeLayout;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public class TimeBarUtil {

    public static <E, I> void setupBarsLayout(ObservableList<E> entities, Function<E, LocalDate> entityDateReader, Function<E, I> entityToBlockConverter, TimeLayout<LocalDateBar<I>, LocalDate> barsLayout) {
        TimeBar.<E, I, LocalDate, LocalDateBar<I>>setupTimeLayout(entities, entityDateReader, entityToBlockConverter, LocalDateBar::new, barsLayout);
    }

    public static <E, I> void setupBarsLayout(ObservableList<E> entities, Function<E, LocalDate> entityDateReader, Function<E, I> entityToBlockConverter, TimeLayout<LocalDateBar<I>, LocalDate> barsLayout, ObservableValue<Boolean> blocksGroupingProperty) {
        TimeBar.<E, I, LocalDate, LocalDateBar<I>>setupTimeLayout(entities, entityDateReader, entityToBlockConverter, LocalDateBar::new, barsLayout, blocksGroupingProperty);
    }
}
