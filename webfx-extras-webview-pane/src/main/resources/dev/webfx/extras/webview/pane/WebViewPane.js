
var javaWebViewPane, redirectConsoleRequested, consoleRedirected;

if (javaWebViewPane)
    injectJavaWebPane(javaWebViewPane);

function injectJavaWebPane(javaWebPaneInstance) {
    javaWebViewPane   = javaWebPaneInstance;
    applyRedirectConsoleNowIfApplicable();
}

function applyRedirectConsoleNowIfApplicable() {
    if (!consoleRedirected && redirectConsoleRequested && javaWebViewPane) {
        console.log   = function(message) { javaWebViewPane.consoleLog(message);   };
        console.warn  = function(message) { javaWebViewPane.consoleWarn(message);  };
        console.error = function(message) { javaWebViewPane.consoleError(message); };
        consoleRedirected = true;
    }
}