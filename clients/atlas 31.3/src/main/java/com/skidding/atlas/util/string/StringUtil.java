package com.skidding.atlas.util.string;


import java.util.regex.Pattern;

public class StringUtil {
    // Matches if a string ends with a single dot
    // Hello. -> matching
    // Hello... -> not matching
    // Hell.o -> not matching
    public static final String DOT_PATTERN = "(?<!\\.)\\.$";
    public static final Pattern DOT_REGEX_PATTERN = Pattern.compile(DOT_PATTERN);

}
