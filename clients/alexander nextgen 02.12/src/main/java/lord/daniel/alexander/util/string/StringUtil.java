package lord.daniel.alexander.util.string;

import lord.daniel.alexander.Modification;
import lord.daniel.alexander.interfaces.IMinecraft;
import net.minecraft.util.EnumChatFormatting;

/**
 * Written by Daniel. on 25/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class StringUtil implements IMinecraft {

    public static String makeReadable(String input) {
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

    public static String format(String text) {
        text = text.replace("%VERSION%", String.valueOf(Modification.VERSION))
                .replace("%BUILD%", String.valueOf(Modification.VERSION))
                .replace("%FPS%", String.valueOf(mc.getDebugFPS()))
                .replace("%MCNAME%", mc.getSession().getUsername())
                .replace("%CLNAME%", "TODO") // TODO: This
                .replace("%PROTOCOL%", "1.8x"); // TODO: Also this

        for (EnumChatFormatting enumChatFormatting : EnumChatFormatting.values()) {
            text = text.replace("%FORMAT_" + enumChatFormatting.getFriendlyName().toUpperCase() + "%", enumChatFormatting.toString());
        }

        return text;
    }

}
