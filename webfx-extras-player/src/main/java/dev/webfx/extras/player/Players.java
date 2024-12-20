package dev.webfx.extras.player;

import dev.webfx.extras.player.impl.PlayerGroupImpl;
import javafx.scene.paint.Color;

/**
 * @author Bruno Salmon
 */
public final class Players {

    private static final PlayerGroup GLOBAL_PLAYER_GROUP = new PlayerGroupImpl(true);

    private static Color GLOBAL_PLAYER_COLOR;

    public static PlayerGroup getGlobalPlayerGroup() {
        return GLOBAL_PLAYER_GROUP;
    }

    public static void setGlobalPlayerColor(Color globalPlayerColor) {
        GLOBAL_PLAYER_COLOR = globalPlayerColor;
    }

    public static Color getGlobalPlayerColor() {
        return GLOBAL_PLAYER_COLOR;
    }

}
