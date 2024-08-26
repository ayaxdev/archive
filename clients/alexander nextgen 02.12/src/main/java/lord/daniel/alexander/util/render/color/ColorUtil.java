package lord.daniel.alexander.util.render.color;

import lombok.experimental.UtilityClass;
import lord.daniel.alexander.util.math.MathUtil;

import java.awt.*;
import java.util.regex.Pattern;

@UtilityClass
public class ColorUtil {

    private final Pattern COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
    public static final Color DEFAULT_COLOR = new Color(61, 157, 239);

    public static final char COLOR_INVOKER = '\247';

    public static final int[] SKY_RAINBOW_COLORS = {
            0xfffc6a8c, 0xfffc6ad5, 0xffda6afc, 0xff916afc, 0xff6a8cfc, 0xff6ad5fc, 0xffda6afc, 0xfffc6a8c,
    };

    public static final int[] CZECHIA_COLORS = {
            0xFF11457E, 0xFF11457E, 0xFFD7141A, 0xFFD7141A, 0xFFFFFFFF, 0xFF11457E,
    };

    public static final int[] GERMAN_COLORS = {
            0xFF000000, 0xFFFE0000, 0xFFFFCF00, 0xFF000000,
    };

    private static final int[] HEALTH_COLOURS = {
            0xFF006B32, 0xFFFFFF00, 0xFFFF8000, 0xFFFF0000, 0xFF800000
    };


    public static int blendColours(final int[] colours, final double progress) {
        final int size = colours.length;
        if (progress == 1.f) return colours[0];
        else if (progress == 0.f) return colours[size - 1];
        final double mulProgress = Math.max(0, (1 - progress) * (size - 1));
        final int index = (int) mulProgress;
        return fadeBetween(colours[index], colours[index + 1], mulProgress - index);
    }


    public static int fadeBetween(int startColour, int endColour, double progress) {
        if (progress > 1) progress = 1 - progress % 1;
        return fadeTo(startColour, endColour, progress);
    }

    public static int fadeBetween(int[] colors, int endColour, long offset) {
        return blendColours(colors, ((System.currentTimeMillis() + offset) % 2000L) / 1000.0);
    }

    public static int fadeBetween(int startColour, int endColour, long offset) {
        return fadeBetween(startColour, endColour, ((System.currentTimeMillis() + offset) % 2000L) / 1000.0);
    }

    public static int fadeBetween(int startColour, int endColour) {
        return fadeBetween(startColour, endColour, 0L);
    }

    public static int fadeTo(int startColour, int endColour, double progress) {
        double invert = 1.0 - progress;
        int r = (int) ((startColour >> 16 & 0xFF) * invert +
                (endColour >> 16 & 0xFF) * progress);
        int g = (int) ((startColour >> 8 & 0xFF) * invert +
                (endColour >> 8 & 0xFF) * progress);
        int b = (int) ((startColour & 0xFF) * invert +
                (endColour & 0xFF) * progress);
        int a = (int) ((startColour >> 24 & 0xFF) * invert +
                (endColour >> 24 & 0xFF) * progress);
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                (b & 0xFF);
    }

    public static int getRainbow(int speed, int offset) {
        float hue = (System.currentTimeMillis() + offset) % speed;
        hue /= speed;
        return Color.getHSBColor(hue, 0.85f, 1f).getRGB();
    }

    public static double getFadingFromSysTime(final long offset) {
        return ((System.currentTimeMillis() + offset) % 2000L) / 2000.0;
    }

    public static int darken(final int color, final float factor) {
        final int r = (int)((color >> 16 & 0xFF) * factor);
        final int g = (int)((color >> 8 & 0xFF) * factor);
        final int b = (int)((color & 0xFF) * factor);
        final int a = color >> 24 & 0xFF;
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) | (a & 0xFF) << 24;
    }

    public Color setAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public Color setAlpha(final Color color, final float alpha) {
        return new Color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, alpha);
    }

    public String fixColorCodes(String text) {
        return text.replace("&", String.valueOf(COLOR_INVOKER));
    }

    public String stripColor(String s) {
        return COLOR_PATTERN.matcher(s).replaceAll("");
    }


    public static Color interpolateColor(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(MathUtil.interpolateInt(color1.getRed(), color2.getRed(), amount),
                MathUtil.interpolateInt(color1.getGreen(), color2.getGreen(), amount),
                MathUtil.interpolateInt(color1.getBlue(), color2.getBlue(), amount),
                MathUtil.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

}
