package dev.webfx.extras.i18n.spi.impl.ast;

import dev.webfx.platform.async.Future;
import dev.webfx.platform.async.Promise;
import dev.webfx.platform.resource.Resource;
import dev.webfx.platform.util.Strings;
import dev.webfx.extras.i18n.Dictionary;
import dev.webfx.extras.i18n.spi.impl.DictionaryLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple resource-based dictionary loader assuming a resource file in AST format (properties or json) for each
 * language. It assumes that the AST formats in use in these resource files are already registered in the AST API.
 * It loads the whole dictionary resource file with all its keys (and not just the requested keys).
 * It doesn't know in advance in which AST format the dictionary will be, so it will try to load them all
 * (ex: en.properties and en.json) and return the one found in the resources (this may cause "... 404 (Not Found)"
 * logs in the browser console). When a dictionary is found in the requested language, it is cached, because it
 * contains all keys and can therefore be used on subsequent keys requests. This cache also prevents repeating
 * resource loading, including failed attempts.
 *
 * @author Bruno Salmon
 */
final class ResourceAstDictionaryLoader implements DictionaryLoader {

    private static final boolean CACHE_DICTIONARIES = true;

    private final String astResourcePathWithLangPattern;
    private final String[] supportedFormats;
    private final Map<Object /* lang */, Dictionary> dictionaryCache = new HashMap<>();

    ResourceAstDictionaryLoader(String astResourcePathWithLangPattern, String... supportedFormats) {
        this.astResourcePathWithLangPattern = astResourcePathWithLangPattern;
        this.supportedFormats = supportedFormats;
    }

    private String getDictionaryResourcePath(Object lang, String format) {
        String path = astResourcePathWithLangPattern;
        path = Strings.replaceAll(path, "{lang}", Strings.toString(lang));
        path = Strings.replaceAll(path, "{format}", Strings.toString(format));
        return path;
    }

    @Override
    public Future<Dictionary> loadDictionary(Object lang, Set<Object> keys) {
        Dictionary cachedDictionary = dictionaryCache.get(lang);
        if (cachedDictionary != null)
            return Future.succeededFuture(cachedDictionary);
        Promise<Dictionary> promise = Promise.promise();
        AtomicInteger failureCounter = new AtomicInteger();
        for (String format : supportedFormats) {
            String dictionaryResourcePath = getDictionaryResourcePath(lang, format);
            Resource.loadText(dictionaryResourcePath, text -> {
                if (text != null) { // null happens on JRE when resource doesn't exist
                    try {
                        AstDictionary dictionary = new AstDictionary(text, format);
                        if (CACHE_DICTIONARIES)
                            dictionaryCache.put(lang, dictionary);
                        promise.tryComplete(dictionary);
                    } catch (Exception e) { // Can happen while parsing the text in the format
                        promise.tryFail(new RuntimeException("⛔️ Format error in i18n dictionary " + dictionaryResourcePath + " - error: " + e.getMessage()));
                    }
                } else { // null = resource not found
                    if (failureCounter.incrementAndGet() == supportedFormats.length)
                        promise.tryFail("No dictionary found for language " + lang);
                }
            }, e -> {
                if (failureCounter.incrementAndGet() == supportedFormats.length)
                    promise.tryFail(e);
            });
        }
        return promise.future();
    }
}
