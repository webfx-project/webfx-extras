package dev.webfx.extras.timelayout.bar;

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

}
