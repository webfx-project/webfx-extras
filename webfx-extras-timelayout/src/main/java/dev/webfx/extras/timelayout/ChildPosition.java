package dev.webfx.extras.timelayout;

/**
 * @author Bruno Salmon
 */
public final class ChildPosition<T> {

    private boolean valid;
    private TimeCell<T> originCell;
    private double x, y, width, height;
    private int rowIndex, columnIndex; // redundant with cell (to be removed later)
    private int columnSpan, rowSpan;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public TimeCell<T> getOriginCell() {
        return originCell;
    }

    public void setOriginCell(TimeCell<T> originCell) {
        this.originCell = originCell;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }
}
