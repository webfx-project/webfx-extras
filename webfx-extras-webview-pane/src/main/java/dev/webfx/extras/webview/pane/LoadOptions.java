package dev.webfx.extras.webview.pane;

import java.util.function.Consumer;

/**
 * @author Bruno Salmon
 */
public class LoadOptions {

    private boolean seamlessInBrowser;
    private String seamlessContainerId;
    private Runnable onWebEngineReady;
    private Runnable onWebWindowReady;
    private Runnable onLoadSuccess;
    private Consumer<String> onLoadFailure;

    public boolean isSeamlessInBrowser() {
        return seamlessInBrowser;
    }

    public LoadOptions setSeamlessInBrowser(boolean seamlessInBrowser) {
        this.seamlessInBrowser = seamlessInBrowser;
        return this;
    }

    public String getSeamlessContainerId() {
        return seamlessContainerId;
    }

    public LoadOptions setSeamlessContainerId(String seamlessContainerId) {
        this.seamlessContainerId = seamlessContainerId;
        return this;
    }

    public Runnable getOnWebEngineReady() {
        return onWebEngineReady;
    }

    public LoadOptions setOnWebEngineReady(Runnable onWebEngineReady) {
        this.onWebEngineReady = onWebEngineReady;
        return this;
    }

    public Runnable getOnWebWindowReady() {
        return onWebWindowReady;
    }

    public LoadOptions setOnWebWindowReady(Runnable onWebWindowReady) {
        this.onWebWindowReady = onWebWindowReady;
        return this;
    }

    public Runnable getOnLoadSuccess() {
        return onLoadSuccess;
    }

    public LoadOptions setOnLoadSuccess(Runnable onApplicationScriptExecuted) {
        this.onLoadSuccess = onApplicationScriptExecuted;
        return this;
    }

    public Consumer<String> getOnLoadFailure() {
        return onLoadFailure;
    }

    public LoadOptions setOnLoadFailure(Consumer<String> onLoadFailure) {
        this.onLoadFailure = onLoadFailure;
        return this;
    }
}
