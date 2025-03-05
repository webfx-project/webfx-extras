package dev.webfx.extras.fonticons;

import dev.webfx.platform.util.Strings;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Bruno Salmon
 */
public interface FontIcon {

    String name();

    default String getDisplayName() {
        String[] words = Strings.removePrefix(name(), "_").split("_");
        return Arrays.stream(words).map(word ->
            Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase()
        ).collect(Collectors.joining(" "));
    }

    char getChar();

    default int getCodePoint() {
        return getChar();
    }

    default String getText() {
        return String.valueOf(getChar());
    }

}
