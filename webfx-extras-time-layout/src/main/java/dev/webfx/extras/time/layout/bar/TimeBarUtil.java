package dev.webfx.extras.time.layout.bar;

import dev.webfx.extras.time.layout.TimeLayout;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.ObservableLists;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public class TimeBarUtil {

    // LocalDate API

    public static <E, B> void setupBarsLayout(ObservableList<E> entities, Function<E, LocalDate> entityDateReader, Function<E, B> entityToBlockConverter, TimeLayout<LocalDateBar<B>, LocalDate> barsLayout) {
        TimeBarUtil.<E, B, LocalDate, LocalDateBar<B>>setupTimeLayout(entities, entityDateReader, entityToBlockConverter, LocalDateBar::new, barsLayout);
    }

    public static <E, B> void setupBarsLayout(ObservableList<E> entities, Function<E, LocalDate> entityDateReader, Function<E, B> entityToBlockConverter, TimeLayout<LocalDateBar<B>, LocalDate> barsLayout, ObservableValue<Boolean> blocksGroupingProperty) {
        TimeBarUtil.<E, B, LocalDate, LocalDateBar<B>>setupTimeLayout(entities, entityDateReader, entityToBlockConverter, LocalDateBar::new, barsLayout, blocksGroupingProperty);
    }

    // Generic API

    static <TB extends TimeBar<?, T>, T> void setTimeBarLayoutTimeReaders(TimeLayout<TB, T> timeLayout) {
        timeLayout.setInclusiveChildStartTimeReader(TimeBar::getStartTime);
        timeLayout.setInclusiveChildEndTimeReader(TimeBar::getEndTime);
    }

    public static <E, B, T, TB extends TimeBar<B, T>> void setupTimeLayout(ObservableList<E> entities, Function<E, T> entityTimeReader, Function<E, B> entityToBlockConverter, TimeBarFactory<B, T, TB> timeBarFactory, TimeLayout<TB, T> timeLayout) {
        setupTimeLayout(entities, entityTimeReader, entityToBlockConverter, timeBarFactory, timeLayout, null);
    }

    public static <E, B, T, TB extends TimeBar<B, T>> void setupTimeLayout(ObservableList<E> entities, Function<E, T> entityTimeReader, Function<E, B> entityToBlockConverter, TimeBarFactory<B, T, TB> timeBarFactory, TimeLayout<TB, T> timeLayout, ObservableValue<Boolean> blocksGroupingProperty) {
        setTimeBarLayoutTimeReaders(timeLayout);
        bindTimeBarsFromEntities(entities, entityTimeReader, entityToBlockConverter, timeBarFactory, timeLayout.getChildren(), blocksGroupingProperty);
    }

    public static <E, B, T, TB extends TimeBar<B, T>> void bindTimeBarsFromEntities(ObservableList<E> entities, Function<E, T> entityTimeReader, Function<E, B> entityToBlockConverter, TimeBarFactory<B, T, TB> timeBarFactory, ObservableList<TB> timeBarObservableList) {
        bindTimeBarsFromEntities(entities, entityTimeReader, entityToBlockConverter, timeBarFactory, timeBarObservableList, null);
    }

    public static <E, B, T, TB extends TimeBar<B, T>> void bindTimeBarsFromEntities(ObservableList<E> entities, Function<E, T> entityTimeReader, Function<E, B> entityToBlockConverter, TimeBarFactory<B, T, TB> timeBarFactory, ObservableList<TB> timeBarObservableList, ObservableValue<Boolean> blocksGroupingProperty) {
        ObservableLists.runNowAndOnListChange(c -> timeBarObservableList.setAll(createTimeBarsFromEntities(entities, entityTimeReader, entityToBlockConverter, timeBarFactory, blocksGroupingProperty == null || blocksGroupingProperty.getValue())), entities);
        if (blocksGroupingProperty != null)
            FXProperties.runOnPropertiesChange(() -> timeBarObservableList.setAll(createTimeBarsFromEntities(entities, entityTimeReader, entityToBlockConverter, timeBarFactory, blocksGroupingProperty.getValue())), blocksGroupingProperty);
    }

    private static <E, B, T, TB extends TimeBar<B, T>> List<TB> createTimeBarsFromEntities(List<E> entities, Function<E, T> entityTimeReader, Function<E, B> entityToBlockConverter, TimeBarFactory<B, T, TB> timeBarFactory, boolean groupBlocks) {
        B lastBlock = null;
        T barStartTime = null, barEndTime = null;
        List<TB> timeBars = new ArrayList<>();
        for (E entity : entities) {
            B newBlock = entityToBlockConverter.apply(entity);
            if (groupBlocks && newBlock.equals(lastBlock))
                barEndTime = entityTimeReader.apply(entity);
            else {
                if (lastBlock != null)
                    timeBars.add(timeBarFactory.createTimeBar(lastBlock, barStartTime, barEndTime));
                lastBlock = newBlock;
                barStartTime = barEndTime = entityTimeReader.apply(entity);
            }
        }
        if (lastBlock != null)
            timeBars.add(timeBarFactory.createTimeBar(lastBlock, barStartTime, barEndTime));
        return timeBars;
    }
}
