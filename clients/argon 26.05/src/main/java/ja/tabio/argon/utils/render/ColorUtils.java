package ja.tabio.argon.utils.render;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ColorUtils {

    public static class Math {

        @Contract(value = "_ -> new", pure = true)
        public static int @NotNull [] extract(int rgb) {
            int red = rgb >> 8 * 2 & 0xFF;
            int green = rgb >> 8 & 0xFF;
            int blue = rgb & 0xFF;
            int alpha = (rgb >> 24) & 0xff;
            return new int[]{red, green, blue, alpha};
        }

    }

}
