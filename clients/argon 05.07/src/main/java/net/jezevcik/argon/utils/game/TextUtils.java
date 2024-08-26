package net.jezevcik.argon.utils.game;

import net.jezevcik.argon.utils.math.MathUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;

/**
 * A set of methods for interacting with Minecraft's Text class
 */
public class TextUtils {

    public static Text gradient(String text, Style base, Color start, Color end) {
        final Color[] colors = MathUtils.Color.interpolate(start, end, text.length());

        final MutableText out = Text.empty();

        final char[] chars = text.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            final char character = chars[i];

            final Text append = Text.literal(Character.toString(character))
                    .setStyle(base.withColor(colors[i].getRGB()));

            out.append(append);
        }

        return out;
    }

}
