package dev.webfx.extras.webview.pane;

import dev.webfx.extras.panes.MonoPane;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.Unregisterable;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.resource.Resource;
import dev.webfx.platform.scheduler.Scheduled;
import dev.webfx.platform.uischeduler.UiScheduler;
import dev.webfx.platform.useragent.UserAgent;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

import java.util.function.Consumer;

/**
 * @author Bruno Salmon
 */
public class WebViewPane extends MonoPane {

    private static final boolean DEBUG = true;

    private static final boolean IS_GLUON = UserAgent.isNative();
    private static final boolean IS_BROWSER = UserAgent.isBrowser();
    private static final WebEngine PARENT_BROWSER_WINDOW_SCRIPT_ENGINE = IS_BROWSER ? new WebEngine() : null;
    private static final JSObject PARENT_BROWSER_WINDOW = IS_BROWSER ? (JSObject) PARENT_BROWSER_WINDOW_SCRIPT_ENGINE.executeScript("window") : null;

    private WebView webView;
    private WebEngine webEngine;
    private Unregisterable webEngineStateListener;
    private JSObject webWindow;
    private boolean redirectConsole;
    private boolean redirectConsoleApplied;
    private boolean isGluonLayoutStabilized;
    private InvalidationListener gluonWidthListener;
    private PendingLoad pendingLoad;
    private boolean webWindowReadyNotified;
    private boolean unloading;
    private boolean fitHeight;
    private Scheduled fitHeightJob;

    public WebViewPane() {
        initWebEngine();
    }

    private void initWebEngine() {
        logDebug("initWebEngine()");
        webView = new WebView();
        webEngine = webView.getEngine();
        webEngine.setOnError(error -> notifyLoadFailure(error.getMessage()));
        webWindow = null;
        redirectConsoleApplied = false;
        isGluonLayoutStabilized = false;
        pendingLoad = null;
        webWindowReadyNotified = false;
        unloading = false;
        /* Not yet supported by WebFX
        engine.getLoadWorker().exceptionProperty().addListener((obs, oldExc, newExc) -> {
            if (newExc != null) {
                Console.log("WebView exception:", newExc);
            }
        });*/
        if (IS_BROWSER) {
            setContent(webView);
            // Also, consuming touch events on the web view to prevent the page scrolling while the user actually just
            // wants to interact with the map (ex: move the map using 1 finger, or scroll the map using 2 fingers).
            setOnTouchMoved(Event::consume);
        } else {
            setContent(null);
        }
        if (webEngineStateListener != null)
            webEngineStateListener.unregister();
        webEngineStateListener = FXProperties.runNowAndOnPropertiesChange(this::processWebEngineState,
                webEngine.getLoadWorker().stateProperty());
    }

    public static boolean isBrowser() {
        return IS_BROWSER;
    }

    public static boolean isGluon() {
        return IS_GLUON;
    }

    public static void executeSeamlessScriptInBrowser(String script) {
        if (PARENT_BROWSER_WINDOW_SCRIPT_ENGINE != null) {
            PARENT_BROWSER_WINDOW_SCRIPT_ENGINE.executeScript(script);
        }
    }

    public boolean isFitHeight() {
        return fitHeight;
    }

    public void setFitHeight(boolean fitHeight) {
        this.fitHeight = fitHeight;
        manageFitHeightJob();
    }

    public WebView getWebView() {
        return webView;
    }

    public WebEngine getWebEngine() {
        return webEngine;
    }

    public boolean isSeamless() {
        if (!IS_BROWSER)
            return false;
        if (pendingLoad == null)
            return false;
        LoadOptions loadOptions = pendingLoad.getLoadOptions();
        return loadOptions != null && loadOptions.isSeamlessInBrowser();
    }

    @Override
    public void setContent(Node content) {
        manageFitHeightJob();
        if (content == getContent())
            return;
        if (content == webView) {
            logDebug("Setting content to webView");
        } else {
            logDebug("Setting content to " + content);
        }
        super.setContent(content);
    }

    private void manageFitHeightJob() {
        if (isWebViewDisplayed()) {
            if (fitHeight && fitHeightJob == null) {
                fitHeightJob = UiScheduler.schedulePeriodic(100, () -> {
                    JSObject window = getWindow();
                    if (window != null) {
                        try {
                            Object height = window.eval("document.documentElement.scrollHeight");
                            if (height instanceof Number) {
                                setPrefHeight(((Number) height).doubleValue());
                            }
                            Console.log(height);
                        } catch (JSException e) {
                            Console.log("Error when evaluating window height: " + e.getMessage());
                        }
                    }
                });
            }
        } else {
            if (fitHeightJob != null) {
                fitHeightJob.cancel();
                fitHeightJob = null;
            }
        }
    }

    public void loadFromUrl(String url, LoadOptions loadOptions, Boolean isGluonLayoutStabilized) {
        logDebug("Request loading url " + url);
        setPendingLoad(PendingLoad.createPendingUrlLoad(url, loadOptions), isGluonLayoutStabilized);
    }

    public void loadFromHtml(String htmlContent, LoadOptions loadOptions, Boolean isGluonLayoutStabilized) {
        logDebug("Request loading html " + htmlContent);
        setPendingLoad(PendingLoad.createPendingHtmlContentLoad(htmlContent, loadOptions), isGluonLayoutStabilized);
    }

    public void loadFromScript(String script, LoadOptions loadOptions, Boolean isGluonLayoutStabilized) {
        logDebug("Request loading from script " + script);
        setPendingLoad(PendingLoad.createPendingScriptLoad(script, loadOptions), isGluonLayoutStabilized);
    }

    private void setPendingLoad(PendingLoad pendingLoad, Boolean isGluonLayoutStabilized) {
        this.pendingLoad = pendingLoad;
        unloading = false;
        if (isGluonLayoutStabilized != null)
            this.isGluonLayoutStabilized = isGluonLayoutStabilized;
        processWebEngineState();
    }

    public void unload() {
        logDebug("Unloading webEngine & webView");
        unloading = true;
        setContent(null);
        webView.getEngine().load(null);
        // Recreating the web engine for possible next load, because Wistia player doesn't start if we reuse the same
        initWebEngine(); // TODO investigate why
    }

    private void displayWebViewIfStabilised() {
        if (IS_GLUON && !isGluonLayoutStabilized)
            return;
        setContent(webView);
    }

    public boolean isWebViewDisplayed() {
        return getContent() == webView;
    }

    public JSObject getWindow() {
        if (IS_GLUON && !isWebViewDisplayed()) // Calling webEngine on Gluon when webView causes a semi crash!
            return null;
        if (isSeamless()) {
            webWindow = PARENT_BROWSER_WINDOW;
        } else {
            if (webWindow != null && webWindow.toString() == null) {
                logDebug("Resetting window because toString() is null");
                webWindow = null;
            }
            if (webWindow == null) {
                webWindowReadyNotified = false;
                try {
                    webWindow = (JSObject) webEngine.executeScript("window");
                    if (DEBUG) {
                        Console.logNative(webWindow);
                    }
                } catch (Exception e) {
                    Console.log("Exception when trying to get window: " + e.getMessage());
                }
            }
        }
        if (!webWindowReadyNotified && webWindow != null) {
            notifyWebWindowReady();
        }
        return webWindow;
    }

    public boolean setWindowMember(String name, Object value) {
        JSObject window = getWindow();
        if (window != null) {
            logDebug("Setting window." + name);
            try {
                window.setMember(name, value);
                return true;
            } catch (JSException e) {
                logDebug("Setting window." + name + " failed: " + e.getMessage());
                return false;
            }
        } else {
            logDebug("Can't set " + name + " as window is null");
            return false;
        }
    }

    public Object callWindow(String name, Object... args) {
        JSObject window = getWindow();
        if (window != null) {
            logDebug("Calling window." + name);
            try {
                return window.call(name, args);
            } catch (JSException e) {
                logDebug("Calling window." + name + " failed: " + e.getMessage());
            }
        } else {
            logDebug("Can't call " + name + " as window is null");
        }
        return null;
    }

    public boolean isRedirectConsole() {
        return redirectConsole;
    }

    public void setRedirectConsole(boolean redirectConsole) {
        this.redirectConsole = redirectConsole;
        applyRedirectConsoleIfApplicable();
    }

    private void applyRedirectConsoleIfApplicable() {
        if (redirectConsole && !redirectConsoleApplied && !isSeamless() && getWindow() != null) {
            webWindow.setMember("redirectConsoleRequested", true);
            webEngine.executeScript("applyRedirectConsoleNowIfApplicable()");
            redirectConsoleApplied = true;
        }
    }

    private void processWebEngineState() {
        boolean seamless = isSeamless();
        WebEngine we = seamless ? PARENT_BROWSER_WINDOW_SCRIPT_ENGINE : webEngine;
        Worker.State state = we.getLoadWorker().getState();
        logDebug("state = " + state + " (seamless = " + seamless + ", webView is " + (isWebViewDisplayed() ? "" : "NOT ") + "displayed, window is " + (getWindow() == null ? "NOT " : "") + "set)");
        if (state == null) // Browser (to remove?)
            return;
        if (unloading) {
            logDebug("Skipping (unloading)");
            return;
        }
        switch (state) {
            case READY: // the user navigates back here (as the browser unloads the iFrame each time it's removed from the DOM)
                if (IS_GLUON && !isGluonLayoutStabilized) {
                    // On mobiles, we need to ensure the web view container position is stabilized BEFORE attaching the OS web
                    // view (otherwise the OS web view position may be wrong)
                    if (pendingLoad != null)
                        waitForGluonLayoutStabilized();
                    //gluonRetryCounter = 0;
                    return;
                } else {
                    if (pendingLoad != null)
                        displayWebViewIfStabilised(); // in case it was not yet done

                    notifyWebEngineReady();

                    if (pendingLoad == null)
                        return;

                    // Executing next task
                    if (pendingLoad.isUrl()) {
                        String url = pendingLoad.getUrl();
                        logDebug("Engine loads url " + url);
                        we.load(url);
                    } else if (pendingLoad.isHtmlContent()) {
                        String htmlContent = pendingLoad.getHtmlContent();
                        logDebug("Engine loads content " + htmlContent);
                        we.loadContent(htmlContent);
                    } else if (pendingLoad.isScript()) {
                        String script = pendingLoad.getScript();
                        if (IS_GLUON) {
                            String htmlContent = "<html>" +
                                              "<head>\n" +
                                              "        <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>\n" +
                                              "        <meta name='viewport' content='user-scalable=no, initial-scale=1, maximum-scale=1, minimum-scale=1'>\n" +
                                              "</head>\n" +
                                              "<body style='width: 100%; height: 100dvh;'><script type='text/javascript'>" + script + "</script></body></html>";
                            logDebug("(Gluon) Engine loads content " + htmlContent);
                            we.loadContent(htmlContent);
                        } else {
                            logDebug("Engine executes script " + script);
                            we.executeScript(script);
                            notifyLoadSuccess();
                        }
                    }
                }
                break;
            case SUCCEEDED:
                displayWebViewIfStabilised(); // in case it was unloaded
                notifyLoadSuccess();
                break;

            case FAILED:
                notifyLoadFailure("FAILED");
                break;

            case CANCELLED:
                notifyLoadFailure("CANCELLED");
                break;

        }
    }

    private void waitForGluonLayoutStabilized() {
        logDebug("waitForGluonLayoutStabilized");
        if (isWebViewDisplayed() && getWidth() > 0) {
            logDebug("Already stabilised");
            onGluonLayoutStabilized();
        } else if (gluonWidthListener == null) {
            // Waiting for application code to call onGluonWebViewStabilized()
            // We set its content to a resizable rectangle, so it will be resized
            setContent(new ResizableRectangle());
            // We add a width listener to react when the container has been resized to a stable size
            widthProperty().addListener(gluonWidthListener = observable -> {
                logDebug("Detected Gluon stabilisation");
                // One-time listener => we remove it
                widthProperty().removeListener(gluonWidthListener);
                gluonWidthListener = null;
                // Now that the container has a stabilized size (which will be the size of the video player), we can
                // move on and attach the webview (will be done in onGluonLayoutStabilized). But we postpone this call
                // with runLater(), otherwise the webview can be displayed at the wrong place (centered on left top
                // corner of this WebViewPane). This postpone ensures the layout is completely finished.
                Platform.runLater(this::onGluonLayoutStabilized);
            });
        }
    }

    public void onGluonLayoutStabilized() {
        logDebug("Gluon stabilised");
        isGluonLayoutStabilized = true;
        displayWebViewIfStabilised();
        processWebEngineState();
    }

    private void notifyWebEngineReady() {
        LoadOptions loadOptions = pendingLoad == null ? null : pendingLoad.getLoadOptions();
        Runnable onWebEngineReady = loadOptions == null ? null : loadOptions.getOnWebEngineReady();
        if (onWebEngineReady != null) {
            logDebug("Calling onWebEngineReady");
            onWebEngineReady.run();
        }
    }

    private void notifyWebWindowReady() {
        webWindowReadyNotified = true; // Important to prevent infinite loop
        setWindowMember("javaWebViewPane", this);
        String webPaneScript = Resource.getText(Resource.toUrl("WebViewPane.js", getClass()));
        webEngine.executeScript(webPaneScript);
        applyRedirectConsoleIfApplicable();

        LoadOptions loadOptions = pendingLoad == null ? null : pendingLoad.getLoadOptions();
        Runnable onWebWindowReady = loadOptions == null ? null : loadOptions.getOnWebWindowReady();
        if (onWebWindowReady != null) {
            logDebug("Calling onWebWindowReady");
            onWebWindowReady.run();
        }
    }

    private void notifyLoadSuccess() {
        LoadOptions loadOptions = pendingLoad == null ? null : pendingLoad.getLoadOptions();
        Runnable onLoadSuccess = loadOptions == null ? null : loadOptions.getOnLoadSuccess();
        if (onLoadSuccess != null) {
            logDebug("Calling onLoadSuccess");
            onLoadSuccess.run();
        }
        //pendingLoad = null;
    }

    private void notifyLoadFailure(String error) {
        LoadOptions loadOptions = pendingLoad == null ? null : pendingLoad.getLoadOptions();
        Consumer<String> onLoadFailure = loadOptions == null ? null : loadOptions.getOnLoadFailure();
        if (onLoadFailure != null) {
            logDebug("Calling onLoadFailure");
            onLoadFailure.accept(error);
        }
        //pendingLoad = null;
    }

    private static void logDebug(String message) {
        if (DEBUG) {
            Console.log(">>>>>>>>>>>>>> " + message);
        }
    }

    // Java callbacks called from JavaScript => must be declared in webfx.xml (required for successful GWT & Gluon compilation)

    public void consoleLog(String message) {
        Console.log("[WebView console.log] " + message);
    }

    public void consoleWarn(String message) {
        Console.log("[WebView console.warn] " + message);
    }

    public void consoleError(String message) {
        Console.log("[WebView console.error] " + message);
    }

}
