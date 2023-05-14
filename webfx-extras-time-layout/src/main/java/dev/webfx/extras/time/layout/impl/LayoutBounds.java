package dev.webfx.extras.time.layout.impl;

import dev.webfx.extras.geometry.MutableBounds;

/**
 * @author Bruno Salmon
 */
public class LayoutBounds<C, T> extends MutableBounds {

    protected final TimeLayoutBase<C, T> timeLayout;
    private C child;
    private T startTime;
    private T endTime;
    protected int tVersion, hVersion, vVersion;
    private int rowIndex, columnIndex;
    private int rowIndexVersion, columnIndexVersion;

    public LayoutBounds(TimeLayoutBase<C, T> timeLayout) {
        this.timeLayout = timeLayout;
        invalidate();
    }

    public C getChild() {
        return child;
    }

    public void setChild(C child) {
        if (child != this.child) {
            invalidate();
            this.child = child;
        }
    }

    protected void invalidate() {
        invalidateT(); // T & H
        invalidateV();
    }

    protected void invalidateT() {
        tVersion = -1;
        invalidateH();
    }

    protected void invalidateH() {
        hVersion = -1;
    }

    protected void invalidateV() {
        vVersion = -1;
    }

    public T getStartTime() {
        checkSyncT();
        return startTime;
    }

    public void setStartTime(T startTime) {
        this.startTime = startTime;
    }

    public T getEndTime() {
        checkSyncT();
        return endTime;
    }

    public void setEndTime(T endTime) {
        this.endTime = endTime;
    }

    protected void checkSyncT() {
        if (tVersion != timeLayout.tVersion) {
            syncT();
            tVersion = timeLayout.tVersion;
        }
    }

    protected void syncT() {
        timeLayout.syncChildT(this);
    }

    @Override
    public double getX() {
        checkSyncH();
        return super.getX();
    }

    @Override
    public double getWidth() {
        checkSyncH();
        return super.getWidth();
    }

    protected void checkSyncH() {
        if (hVersion != timeLayout.hVersion) {
            syncH();
            hVersion = timeLayout.hVersion;
        }
    }

    protected void syncH() {
        timeLayout.syncChildH(this);
    }

    @Override
    public double getY() {
        checkSyncV();
        return super.getY();
    }

    @Override
    public double getHeight() {
        checkSyncV();
        return super.getHeight();
    }

    protected void checkSyncV() {
        if (vVersion != timeLayout.vVersion) {
            syncV();
            vVersion = timeLayout.vVersion;
        }
    }

    protected void syncV() {
        timeLayout.syncChildV(this);
    }

    public int getRowIndex() {
        if (rowIndexVersion != timeLayout.vVersion)
            timeLayout.syncChildRowIndex(this);
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
        rowIndexVersion = timeLayout.vVersion;
    }

    public int getColumnIndex() {
        if (columnIndexVersion != timeLayout.hVersion)
            timeLayout.syncChildColumnIndex(this);
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
        columnIndexVersion = timeLayout.hVersion;
    }

}
