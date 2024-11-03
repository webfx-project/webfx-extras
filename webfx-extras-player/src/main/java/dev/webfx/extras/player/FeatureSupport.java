package dev.webfx.extras.player;

import dev.webfx.extras.player.impl.FeatureSupportImpl;

/**
 * Indicates what level of support the player is offering for a feature (ex: navigation, mute, fullscreen).
 *
 * Note that this is once the media is loaded. Features available on start are different and should be requested
 * via Player.getSupportedStartOptions().
 *
 * @author Bruno Salmon
 */
public interface FeatureSupport {

    FeatureSupport FULL_SUPPORT = create(true,  true,  false,  true);
    FeatureSupport NO_SUPPORT   = create(false, false, false, false);
    FeatureSupport USER_SUPPORT = create(true,  false, false,false);
    FeatureSupport USER_AND_API_BY_RESTART_SUPPORT = create(true,  false, true,false);

    default boolean none() {
        return !userControls() && !api() && !apiByRestart() && !notification();
    }

    default boolean full() {
        return userControls() && api() && notification();
    }

    default boolean partial() {
        return !none() && !full();
    }

    boolean userControls();

    boolean api();

    boolean apiByRestart();

    boolean notification();

    static FeatureSupport create(boolean userControls, boolean api, boolean apiByRestart, boolean notification) {
        return new FeatureSupportImpl(userControls, api, apiByRestart, notification);
    }
}
