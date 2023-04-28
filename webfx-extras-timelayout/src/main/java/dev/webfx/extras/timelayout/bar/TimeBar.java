package dev.webfx.extras.timelayout.bar;

import dev.webfx.extras.timelayout.TimeLayout;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.ObservableLists;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TimeBar<I, T> {

    private final I instance;
    private final T startTime;
    private final T endTime;

    public TimeBar(I instance, T startTime, T endTime) {
        this.instance = instance;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public I getInstance() {
        return instance;
    }

    public T getStartTime() {
        return startTime;
    }

    public T getEndTime() {
        return endTime;
    }
    
    
    static <TB extends TimeBar<?, T>, T> void setTimeBarLayoutTimeReaders(TimeLayout<TB, T> timeLayout) {
        timeLayout.setInclusiveChildStartTimeReader(TimeBar::getStartTime);
        timeLayout.setInclusiveChildEndTimeReader(TimeBar::getEndTime);
    }

    public static <A, B, T, TB extends TimeBar<B, T>> void setupTimeLayout(ObservableList<A> aList, Function<A, T> aDateGetter, Function<A, B> aToBConverter, TimeBarFactory<B, T, TB> constructor, TimeLayout<TB, T> timeLayout) {
        setupTimeLayout(aList, aDateGetter, aToBConverter, constructor, timeLayout, null);
    }

    public static <A, B, T, TB extends TimeBar<B, T>> void setupTimeLayout(ObservableList<A> aList, Function<A, T> aDateGetter, Function<A, B> aToBConverter, TimeBarFactory<B, T, TB> constructor, TimeLayout<TB, T> timeLayout, ObservableValue<Boolean> groupProperty) {
        setTimeBarLayoutTimeReaders(timeLayout);
        bindLists(aList, aDateGetter, aToBConverter, constructor, timeLayout.getChildren(), groupProperty);
    }

    public static <A, B, T, TB extends TimeBar<B, T>> void bindLists(ObservableList<A> aList, Function<A, T> aDateGetter, Function<A, B> aToBConverter, TimeBarFactory<B, T, TB> constructor, ObservableList<TB> bList) {
        bindLists(aList, aDateGetter, aToBConverter, constructor, bList, null);
    }

    public static <A, B, T, TB extends TimeBar<B, T>> void bindLists(ObservableList<A> aList, Function<A, T> aDateGetter, Function<A, B> aToBConverter, TimeBarFactory<B, T, TB> constructor, ObservableList<TB> bList, ObservableValue<Boolean> groupProperty) {
        ObservableLists.runNowAndOnListChange(c -> bList.setAll(transformScheduledResourcesToRepeatedDateBlocks(aList, aDateGetter, aToBConverter, constructor, groupProperty == null || groupProperty.getValue())), aList);
        if (groupProperty != null)
            FXProperties.runOnPropertiesChange(() -> bList.setAll(transformScheduledResourcesToRepeatedDateBlocks(aList, aDateGetter, aToBConverter, constructor, groupProperty.getValue())), groupProperty);
    }

    private static <A, B, T, TB extends TimeBar<B, T>> List<TB> transformScheduledResourcesToRepeatedDateBlocks(List<A> aList, Function<A, T> aDateGetter, Function<A, B> aToBConverter, TimeBarFactory<B, T, TB> constructor, boolean group) {
        B lastB = null;
        T firstDate = null, lastDate = null;
        List<TB> timeBars = new ArrayList<>();
        for (A a : aList) {
            B newB = aToBConverter.apply(a);
            if (group && newB.equals(lastB))
                lastDate = aDateGetter.apply(a);
            else {
                if (lastB != null)
                    timeBars.add(constructor.createTimeBar(lastB, firstDate, lastDate));
                lastB = newB;
                firstDate = lastDate = aDateGetter.apply(a);
            }
        }
        if (lastB != null)
            timeBars.add(constructor.createTimeBar(lastB, firstDate, lastDate));
        return timeBars;
    }
    
}
