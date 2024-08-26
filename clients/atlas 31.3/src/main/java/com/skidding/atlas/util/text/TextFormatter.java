package com.skidding.atlas.util.text;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public record TextFormatter(Map<String, Supplier<Object>> placeholders,
                            Map<String, Function<String, Object>> functions) {

    public String get(final String text) {
        String placeholdersAdded = text;

        for (Map.Entry<String, Supplier<Object>> placeholderEntry : placeholders.entrySet()) {
            try {
                placeholdersAdded = placeholdersAdded.replace(placeholderEntry.getKey(), placeholderEntry.getValue().get().toString());
            } catch (Exception e) {
                placeholdersAdded = placeholdersAdded.replace(placeholderEntry.getKey(), "Error");
            }
        }

        final String[] splitBySpace = placeholdersAdded.split("\\s");

        final StringBuilder functionsRanStringBuilder = new StringBuilder();

        for (int i = 0; i < splitBySpace.length; i++) {
            final String current = splitBySpace[i];
            final boolean last = i == splitBySpace.length - 1;

            String output = current;

            if (StringUtils.countMatches(current, "(") != 1 || StringUtils.countMatches(current, ")") != 1) {
                functionsRanStringBuilder.append(output).append(last ? "" : " ");
                continue;
            }

            if (current.indexOf(")") <= current.indexOf("(")) {
                functionsRanStringBuilder.append(output).append(last ? "" : " ");
                continue;
            }


            for (Map.Entry<String, Function<String, Object>> functionEntry : functions.entrySet()) {
                if (current.startsWith(functionEntry.getKey())) {
                    final String subString = current.substring(current.indexOf("(") + 1, current.indexOf(")"));

                    output = functionEntry.getValue().apply(subString).toString();
                }
            }

            functionsRanStringBuilder.append(output).append(last ? "" : " ");
        }

        return functionsRanStringBuilder.toString()
                .replace("\\\\", "\\")
                .replace("&", "\u00a7");
    }

}
