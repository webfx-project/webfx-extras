package dev.webfx.extras.time.layout.impl;

/**
 * This class is a LazyObjectBounds specialised for the children of a TimeLayout. Compared to LazyObjectBounds, it holds
 * some additional input information (start & end times) and some additional layout outputs (row & column indexes - for
 * calendar-based TimeLayout). These new fields are also lazy (read or computed only when required).
 *
 * @author Bruno Salmon
 */
public class ChildBounds<C, T> extends LazyObjectBounds<C> {

    protected final TimeLayoutBase<C, T> timeLayout;
    private T startTime, endTime;
    private int rowIndex, columnIndex;
    private int timeVersion, rowIndexVersion, columnIndexVersion;

    public ChildBounds(TimeLayoutBase<C, T> timeLayout) {
        super(timeLayout);
        this.timeLayout = timeLayout;
        invalidateObject();
    }

    @Override
    public void invalidateObject() {
        super.invalidateObject();
        invalidateTimes();
        invalidateRowIndex();
        invalidateColumnIndex();
    }

    public final T getStartTime() {
        checkLazyTimesReading();
        return startTime;
    }

    public final void setStartTime(T startTime) {
        this.startTime = startTime;
    }

    public final T getEndTime() {
        checkLazyTimesReading();
        return endTime;
    }

    public final void setEndTime(T endTime) {
        this.endTime = endTime;
    }

    private void checkLazyTimesReading() {
        if (!isTimesReadingValid()) {
            readTimes();
            validateTimesReading();
        }
    }

    private boolean isTimesReadingValid() {
        return timeVersion == timeLayout.timeVersion;
    }

    private void validateTimesReading() {
        timeVersion = timeLayout.timeVersion;
    }

    protected void invalidateTimes() {
        timeVersion = -1;
        invalidateHorizontalLayout();
    }

    private void readTimes() {
        timeLayout.readChildTimes(this);
    }

    protected void layoutHorizontally() {
        timeLayout.layoutChildHorizontally(this);
    }


    protected void layoutVertically() {
        timeLayout.layoutChildVertically(this);
    }

    private boolean isRowIndexValid() {
        return rowIndexVersion == timeLayout.verticalVersion;
    }

    private void validateRowIndex() {
        rowIndexVersion = timeLayout.verticalVersion;
    }

    private void invalidateRowIndex() {
        rowIndexVersion = -1;
    }

    public int getRowIndex() {
        if (!isRowIndexValid())
            timeLayout.computeChildRowIndex(this);
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
        validateRowIndex();
    }

    private boolean isColumnIndexValid() {
        return columnIndexVersion == timeLayout.horizontalVersion;
    }

    private void validateColumnIndex() {
        columnIndexVersion = timeLayout.horizontalVersion;
    }

    private void invalidateColumnIndex() {
        columnIndexVersion = -1;
    }

    public int getColumnIndex() {
        if (!isColumnIndexValid())
            timeLayout.computeChildColumnIndex(this);
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
        validateColumnIndex();
    }

}
