package dev.webfx.extras.time.window;

/**
 * Usage:
 *
 *     try (TimeWindowTransaction closable = TimeWindowTransaction.open()) {
 *         setTimeWindowStart(timeWindowStart); // Binding code can call TimeWindowTransaction.isInTimeWindowTransaction() to check if in a transaction
 *     }
 *     setTimeWindowEnd(timeWindowEnd);
 *
 * @author Bruno Salmon
 */
public final class TimeWindowTransaction implements AutoCloseable {

    private static final ThreadLocal<Boolean> THREAD_LOCAL = new ThreadLocal<>();

    private final Boolean previousValue = THREAD_LOCAL.get();

    private TimeWindowTransaction() {
        THREAD_LOCAL.set(Boolean.TRUE);
    }

    public static TimeWindowTransaction open() {
        return new TimeWindowTransaction();
    }

    public static boolean isInTimeWindowTransaction() {
        return Boolean.TRUE.equals(THREAD_LOCAL.get());
    }

    @Override
    public void close() { // Automatically called on try block exit (unless open() returned null)
        THREAD_LOCAL.set(previousValue);
    }

}