package dev.webfx.extras.timelayout.bar;

public interface TimeBarFactory<B, T, TB extends TimeBar<B, T>> {

    TB createTimeBar(B block, T startTime, T endTime);

}
