package dev.webfx.extras.time.layout.bar;

public interface TimeBarFactory<I, T, TB extends TimeBar<I, T>> {

    TB createTimeBar(I instance, T startTime, T endTime);

}
