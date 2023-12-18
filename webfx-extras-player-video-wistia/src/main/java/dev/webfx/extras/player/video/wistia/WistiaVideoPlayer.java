package dev.webfx.extras.player.video.wistia;

import dev.webfx.extras.player.Status;
import dev.webfx.extras.player.video.IntegrationMode;
import dev.webfx.extras.player.video.impl.WebEmbedVideoPlayerBase;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.useragent.UserAgent;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;

/**
 * @author Bruno Salmon
 */
public class WistiaVideoPlayer extends WebEmbedVideoPlayerBase {

    private static final boolean IS_BROWSER = UserAgent.isBrowser();
    private static final WebEngine MAIN_WINDOW_SCRIPT_ENGINE = IS_BROWSER ? new WebEngine() : null;

    static {
        if (IS_BROWSER) {
            MAIN_WINDOW_SCRIPT_ENGINE.executeScript("window._wq = window._wq || [];" +
                    "window.webfx_extras_wistia_videos = {};" +
                    "const script = document.createElement('script');\n" +
                    "script.src = '//fast.wistia.com/assets/external/E-v1.js';\n" +
                    "document.head.appendChild(script);"
            );
        }
    }

    private void callVideo(String script) {
        Console.log("Calling: " + script);
        String track = getCurrentTrack();
        MAIN_WINDOW_SCRIPT_ENGINE.executeScript("const video = window.webfx_extras_wistia_videos['" + track + "'];" +
                " if (video) {" + script +
                "} else {" +
                "    _wq.push({ id: '" + track + "', onReady: function(video) {\n" +
                "    if (!window.webfx_extras_wistia_videos['" + track + "']) {\n" +
                "    " + script + "}\n" +
                "    window.webfx_extras_wistia_videos['" + track + "'] = video;\n" +
                "}});" +
                "}"
        );
    }

    private final Node seamlessContainer = IS_BROWSER ? new WistiaDivNode() : null;

    public WistiaVideoPlayer() {
        super(isSeamless() ? IntegrationMode.SEAMLESS : IntegrationMode.EMBEDDED);
    }

    public static boolean isSeamless() {
        return IS_BROWSER;
    }

    @Override
    public Node getVideoView() {
        return IS_BROWSER ? seamlessContainer : super.getVideoView();
    }

    @Override
    public void play() {
        if (!IS_BROWSER)
            super.play();
        else {
            String track = getCurrentTrack();
            seamlessContainer.getStyleClass().setAll("wistia_embed", "wistia_async_" + track);
            callVideo("video.play()");
            setStatus(Status.PLAYING);
        }
    }

    @Override
    public void pause() {
        if (!IS_BROWSER)
            super.pause();
        else {
            callVideo("video.pause()");
            setStatus(Status.PAUSED);
        }
    }

    @Override
    public void stop() {
        if (!IS_BROWSER)
            super.stop();
        else {
            callVideo("video.pause()");
            setStatus(Status.STOPPED);
        }
    }

    @Override
    protected String trackUrl(String track) {
        return "https://fast.wistia.net/embed/iframe/" + track + "?autoplay=true&playerColor=EE7130";
    }

}
