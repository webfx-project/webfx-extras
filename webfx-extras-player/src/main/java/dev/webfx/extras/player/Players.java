package dev.webfx.extras.player;

import dev.webfx.extras.player.impl.PlayerGroupImpl;
import dev.webfx.extras.player.multi.MultiPlayer;
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

    public static boolean isMaybePlaying(Player player) {
        if (player == null)
            return false;
        if (player.getNavigationSupport().notification())
            return player.isPlaying();
        switch (player.getStatus()) {
            case READY:
            case PLAYING:
                return true;
        }
        return false;
    }

    public static Player getSelectedPlayer(Player player) {
        return player instanceof MultiPlayer ? ((MultiPlayer) player).getSelectedPlayer() : player;
    }

    public static boolean sameSelectedPlayer(Player p1, Player p2) {
        return getSelectedPlayer(p1) == getSelectedPlayer(p2);
    }

}
