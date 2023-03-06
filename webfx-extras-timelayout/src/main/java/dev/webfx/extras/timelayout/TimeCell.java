package dev.webfx.extras.timelayout;

public interface TimeCell<T> {

    TimeRow getRow();

    TimeColumn<T> getColumn();

}
