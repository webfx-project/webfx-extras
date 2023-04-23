package dev.webfx.extras.timelayout.util;

import dev.webfx.platform.scheduler.Scheduled;
import dev.webfx.platform.uischeduler.UiScheduler;

/**
 * This utility class offers a markAsDirty() method that will run the passed cleaner runnable in the next animation
 * frame. For performance optimisation, the cleaner will run only once even if many calls to markAsDirty() are made.
 * This can be used to postpone time-consuming operations, such as layout or canvas redraw.
 *
 * @author Bruno Salmon
 */
public final class DirtyMarker {

    private final Runnable cleaner;

    private Scheduled cleanerScheduled;

    public DirtyMarker(Runnable cleaner) {
        this.cleaner = cleaner;
    }

    public void markAsDirty() {
        if (cleanerScheduled != null)
            return;
        cleanerScheduled = UiScheduler.scheduleInAnimationFrame(() -> {
            cleaner.run();
            cleanerScheduled = null;
        });
    }

}
