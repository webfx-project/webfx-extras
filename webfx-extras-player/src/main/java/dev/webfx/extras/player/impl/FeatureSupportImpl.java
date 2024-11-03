package dev.webfx.extras.player.impl;

import dev.webfx.extras.player.FeatureSupport;

/**
 * @author Bruno Salmon
 */
public final class FeatureSupportImpl implements FeatureSupport {

    private final boolean userControls;
    private final boolean api;
    private final boolean apiByRestart;
    private final boolean notification;

    public FeatureSupportImpl(boolean userControls, boolean api, boolean apiByRestart, boolean notification) {
        this.userControls = userControls;
        this.api = api;
        this.apiByRestart = apiByRestart;
        this.notification = notification;
    }

    @Override
    public boolean userControls() {
        return userControls;
    }

    @Override
    public boolean api() {
        return api;
    }

    @Override
    public boolean apiByRestart() {
        return apiByRestart;
    }

    @Override
    public boolean notification() {
        return notification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeatureSupportImpl that = (FeatureSupportImpl) o;
        return userControls == that.userControls && api == that.api && notification == that.notification;
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(userControls);
        result = 31 * result + Boolean.hashCode(api);
        result = 31 * result + Boolean.hashCode(notification);
        return result;
    }
}
