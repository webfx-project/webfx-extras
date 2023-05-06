package dev.webfx.extras.canvas.pane;

public interface CanvasRefresher {

    void refreshCanvas(double virtualCanvasWidth, double virtualCanvasHeight, double virtualViewPortY, boolean canvasSizeChanged);

}
