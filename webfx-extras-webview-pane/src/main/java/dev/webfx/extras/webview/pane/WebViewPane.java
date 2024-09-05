package dev.webfx.extras.webview.pane;

import dev.webfx.extras.panes.MonoPane;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.Unregisterable;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.resource.Resource;
import dev.webfx.platform.scheduler.Scheduled;
import dev.webfx.platform.scheduler.Scheduler;
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

    private static final boolean DEBUG = false;

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
    private boolean loadSuccessNotified;
    private boolean unloading;
    private boolean fitHeight;
    private double fitHeightExtra;
    private Scheduled fitHeightJob;
    private boolean urlLoaded;

    public WebViewPane() {
        initWebEngine();
    }

    private void initWebEngine() {
        logDebug("initWebEngine()");
        webView = new WebView();
        webEngine = webView.getEngine();
        webEngine.setOnError(error -> notifyLoadFailure(error.getMessage()));
        pendingLoad = null;
        urlLoaded = false;
        resetState();
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

    private void resetState() {
        logDebug("Resetting state");
        webWindow = null;
        redirectConsoleApplied = false;
        isGluonLayoutStabilized = false;
        webWindowReadyNotified = false;
        loadSuccessNotified = false;
        unloading = false;
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

    public double getFitHeightExtra() {
        return fitHeightExtra;
    }

    public void setFitHeightExtra(double fitHeightExtra) {
        this.fitHeightExtra = fitHeightExtra;
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
        if (!isWebViewDisplayed()) {
            if (fitHeightJob != null) {
                fitHeightJob.cancel();
                fitHeightJob = null;
            }
        } else if (fitHeight && fitHeightJob == null) {
            new Runnable() {
                double lastHeight;
                long lastHeightChangedTime;
                @Override
                public void run() {
                    // Stopping the periodic scheduling when this WebViewPane is not in the scene graph anymore
                    if (getScene() == null && lastHeightChangedTime != 0) { // but waiting at least a change to prevent stopping it initially
                        fitHeightJob = null;
                        return;
                    }
                    double newHeight = 0;
                    if (isSeamless()) {
                        Node seamlessContainer = getContent();
                        if (seamlessContainer != null) // Actually expecting SeamlessDiv, so prefHeight() will actually be measure by the browser
                            newHeight = seamlessContainer.prefHeight(getWidth()) + fitHeightExtra;
                    } else {
                        JSObject window = getWindow();
                        if (window != null) {
                            try {
                                // Evaluating the max height over the document and all possible iFrames
                                Object heightEval = window.eval(
                                        "var maxHeight = document.documentElement.scrollHeight;\n" +
                                        "document.querySelectorAll('iframe').forEach(function(iframe) {\n" +
                                        "    let style = window.getComputedStyle(iframe);\n" +
                                        "    if (style.getPropertyValue('display') !== 'none' && style.getPropertyValue('visibility') !== 'hidden' && style.getPropertyValue('opacity') !== '0')\n" +
                                        "       maxHeight = Math.max(maxHeight, iframe.scrollHeight);\n" +
                                        "});\n" +
                                        "maxHeight;");
                                if (heightEval instanceof Number)
                                    newHeight = ((Number) heightEval).doubleValue() + fitHeightExtra;
                            } catch (JSException e) {
                                Console.log("Error when evaluating window height: " + e.getMessage());
                            }
                        }
                    }
                    long now = System.currentTimeMillis();
                    if (newHeight > 0 && newHeight != lastHeight) {
                        setPrefHeight(newHeight);
                        lastHeight = newHeight;
                        lastHeightChangedTime = now;
                    }
                    boolean heightChanging = now - lastHeightChangedTime < 1000;
                    fitHeightJob = UiScheduler.scheduleDelay(heightChanging ? 10 : 100, this);
                }
            }.run();
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

    private void displayWebViewIfApplicableAndStabilised() {
        if (isSeamless()) // Not applicable
            return;
        if (IS_GLUON && !isGluonLayoutStabilized) // Not stabilised
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
        if (unloading) {
            logDebug("Skipping (unloading)");
            return;
        }
        switch (state) {

            case READY: // the user navigates back here (as the browser unloads the iFrame each time it's removed from the DOM)
                if (loadSuccessNotified) {
                    resetState();
                }
                if (IS_GLUON && !isGluonLayoutStabilized) {
                    // On mobiles, we need to ensure the web view container position is stabilized BEFORE attaching the OS web
                    // view (otherwise the OS web view position may be wrong)
                    if (pendingLoad != null)
                        waitForGluonLayoutStabilized();
                    //gluonRetryCounter = 0;
                    return;
                } else {
                    if (pendingLoad != null)
                        displayWebViewIfApplicableAndStabilised(); // in case it was not yet done

                    notifyWebEngineReady();

                    if (pendingLoad == null)
                        return;

                    // Executing next task
                    if (pendingLoad.isUrl()) {
                        if (!urlLoaded) {
                            String url = pendingLoad.getUrl();
                            logDebug("Engine loads url " + url);
                            we.load(url);
                            urlLoaded = true;
                        }
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
                            if (isSeamless()) {
                                LoadOptions loadOptions = pendingLoad.getLoadOptions();
                                SeamlessDiv seamlessDiv;
                                Node currentContent = getContent();
                                if (currentContent instanceof SeamlessDiv) {
                                    seamlessDiv = (SeamlessDiv) currentContent;
                                } else {
                                    seamlessDiv = new SeamlessDiv();
                                    setContent(seamlessDiv);
                                }
                                String seamlessContainerId = loadOptions == null ? null : loadOptions.getSeamlessContainerId();
                                if (seamlessContainerId != null) {
                                    seamlessDiv.setId(seamlessContainerId);
                                }
                                if (loadOptions != null && loadOptions.getSeamlessStyleClass() != null) {
                                    seamlessDiv.getStyleClass().setAll(loadOptions.getSeamlessStyleClass());
                                }
                                // Postponing the script execution. The reason for this is that if we just created a
                                // seamlessDiv, it's only in the JavaFX scene graph at this stage, it will be mapped
                                // by webfx a bit later (in the next animation frame), but the script probably needs
                                // access it straightaway (ex: seamless video player), so we postpone its execution
                                // to ensure webfx inserted it in the DOM.
                                UiScheduler.scheduleDeferred(() -> {
                                    executeSeamlessScriptInBrowser(script);
                                    notifyLoadSuccess();
                                });
                            } else if (webWindow != null) {
                                logDebug("Engine executes script " + script);
                                we.executeScript(script);
                                notifyLoadSuccess();
                            } else {
                                Scheduler.scheduleDelay(100, this::processWebEngineState);
                            }
                        }
                    }
                }
                break;

            case SUCCEEDED:
                displayWebViewIfApplicableAndStabilised(); // in case it was unloaded
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
        displayWebViewIfApplicableAndStabilised();
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
        if (loadSuccessNotified)
            return;
        LoadOptions loadOptions = pendingLoad == null ? null : pendingLoad.getLoadOptions();
        Runnable onLoadSuccess = loadOptions == null ? null : loadOptions.getOnLoadSuccess();
        if (onLoadSuccess != null) {
            loadSuccessNotified = true;
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
