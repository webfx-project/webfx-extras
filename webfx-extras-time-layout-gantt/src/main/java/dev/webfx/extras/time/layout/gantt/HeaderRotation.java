package dev.webfx.extras.time.layout.gantt;

public enum HeaderRotation {

    NO_ROTATION(0),
    DEG_90_ANTICLOCKWISE(-90),
    DEG_90_CLOCKWISE(90),

    DEG_180(180);

    private final double angle;

    HeaderRotation(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }

    public boolean isRotated() {
        return this != NO_ROTATION;
    }

}
