package dev.webfx.extras.theme;


import dev.webfx.platform.scheduler.Scheduled;
import dev.webfx.platform.uischeduler.UiScheduler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public class ThemeRegistry {

    private final static List<Theme> THEMES = new ArrayList<>();
    private final static List<WeakReference<Facet>> FACETS = new ArrayList<>();
    private final static List<Runnable> MODE_CHANGE_LISTENERS = new ArrayList<>();


    public static void registerTheme(Theme theme) {
        THEMES.add(theme);
    }

    private static Scheduled purgeScheduled;

    public static void styleFacet(Facet facet) {
        FACETS.add(new WeakReference<>(facet));
        facet.getContainerNode().getProperties().put("facet", facet);
        styleFacetNow(facet);
        if (purgeScheduled == null)
            purgeScheduled = UiScheduler.scheduleDelay(1000, () -> {
                purgeGarbageCollectedFacets(false);
                purgeScheduled = null;
            });
    }

    public static void styleFacetNow(Facet facet) {
        styleFacetNow(facet, facet.getFacetCategory());
    }

    public static void styleFacetNow(Facet facet, Object facetCategory) {
        // THEMES.stream().filter(s -> s.supportsFacetCategory(facetCategory)).forEach(s -> s.styleFacet(facet, facetCategory)); // May throw ConcurrentModificationException
        for (int i = 0; i < THEMES.size(); i++) {
            Theme theme = THEMES.get(i);
            if (theme.supportsFacetCategory(facetCategory))
                theme.styleFacet(facet, facetCategory);
        }
    }

    public static void fireModeChanged() {
        purgeGarbageCollectedFacets(true);
        fireModeChangedImpl();
    }

    public static void addModeChangeListener(Runnable listener) {
        MODE_CHANGE_LISTENERS.add(listener);
    }

    public static void runNowAndOnModeChange(Runnable listener) {
        listener.run();
        addModeChangeListener(listener);
    }

    public static void removeModeChangeListener(Runnable listener) {
        MODE_CHANGE_LISTENERS.remove(listener);
    }

    private static void fireModeChangedImpl() {
        MODE_CHANGE_LISTENERS.forEach(Runnable::run);
    }

    private static void purgeGarbageCollectedFacets(boolean applyStyle) {
        for (Iterator<WeakReference<Facet>> it = FACETS.iterator(); it.hasNext(); ) {
            WeakReference<Facet> weakReference = it.next();
            Facet facet = weakReference.get();
            if (facet == null) {
                //Console.log("Removed weak ref!!!");
                it.remove();
            } else if (applyStyle)
                styleFacetNow(facet);
        }
        //Console.log("FACETS.size() = " + FACETS.size());
    }

}
