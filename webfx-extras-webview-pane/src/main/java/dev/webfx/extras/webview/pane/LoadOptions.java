package dev.webfx.extras.webview.pane;

/**
 * @author Bruno Salmon
 */
public class LoadOptions {

    private boolean seamlessInBrowser;
    private Runnable onWebEngineReady;
    private Runnable onWebWindowReady;
    private Runnable onLoadSuccess;

    public boolean isSeamlessInBrowser() {
        return seamlessInBrowser;
    }

    public LoadOptions setSeamlessInBrowser(boolean seamlessInBrowser) {
        this.seamlessInBrowser = seamlessInBrowser;
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

}
