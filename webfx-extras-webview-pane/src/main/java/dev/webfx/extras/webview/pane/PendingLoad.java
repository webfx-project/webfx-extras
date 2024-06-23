package dev.webfx.extras.webview.pane;

/**
 * @author Bruno Salmon
 */
final class PendingLoad {

    private final String url;
    private final String htmlContent;
    private final String script;
    private final LoadOptions loadOptions;

    private PendingLoad(String url, String htmlContent, String script, LoadOptions loadOptions) {
        this.url = url;
        this.htmlContent = htmlContent;
        this.script = script;
        this.loadOptions = loadOptions;
    }

    public String getUrl() {
        return url;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public String getScript() {
        return script;
    }

    public LoadOptions getLoadOptions() {
        return loadOptions;
    }

    public boolean isUrl() {
        return url != null;
    }

    public boolean isHtmlContent() {
        return htmlContent != null;
    }

    public boolean isScript() {
        return script != null;
    }

    static PendingLoad createPendingUrlLoad(String url, LoadOptions loadOptions) {
        return new PendingLoad(url, null, null, loadOptions);
    }

    static PendingLoad createPendingHtmlContentLoad(String htmlContent, LoadOptions loadOptions) {
        return new PendingLoad(null, htmlContent, null, loadOptions);
    }

    static PendingLoad createPendingScriptLoad(String script, LoadOptions loadOptions) {
        return new PendingLoad(null, null, script, loadOptions);
    }
}
