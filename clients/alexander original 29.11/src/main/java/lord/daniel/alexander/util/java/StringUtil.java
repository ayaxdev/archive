package lord.daniel.alexander.util.java;

import lombok.experimental.UtilityClass;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.interfaces.Methods;
import net.minecraft.util.EnumChatFormatting;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@UtilityClass
public class StringUtil implements Methods {

    public String format(String text) {
        text = text.replace("%VERSION%", Modification.VERSION)
                .replace("%BUILD%", Modification.BUILD_NUMBER)
                .replace("%FPS%", String.valueOf(mc.getDebugFPS()))
                .replace("%MCNAME%", mc.getSession().getUsername())
                .replace("%CLNAME%", "TODO") // TODO: This
                .replace("%PROTOCOL%", "1.8x"); // TODO: Also this

        for(EnumChatFormatting enumChatFormatting : EnumChatFormatting.values()) {
            text = text.replace("%FORMAT_" + enumChatFormatting.getFriendlyName().toUpperCase() + "%", enumChatFormatting.toString());
        }

        return text;
    }

    public static String convertToReadableString(String input) {
        String[] words = input.split("_");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            // Convert the word to lowercase, except for the first character
            String formattedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            result.append(formattedWord).append(" ");
        }

        // Remove the trailing space and return the formatted string
        return result.toString().trim();
    }

    public static String convertToOriginalFormat(String readableString) {
        String[] words = readableString.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            // Convert the word to uppercase and append it to the result, separated by underscore
            String formattedWord = word.toUpperCase();
            result.append(formattedWord).append("_");
        }

        // Remove the trailing underscore and return the original format
        return result.toString().substring(0, result.length() - 1);
    }

}
