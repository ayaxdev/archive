package com.skidding.atlas.util.render.color;

import com.skidding.atlas.util.math.MathUtil;

import java.awt.*;

public class ColorUtil {

    //The next few methods are for interpolating colors
    public static int interpolateColor(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return interpolateColorC(color1, color2, amount).getRGB();
    }

    public static int interpolateColor(int color1, int color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        Color cColor1 = new Color(color1);
        Color cColor2 = new Color(color2);
        return interpolateColorC(cColor1, cColor2, amount).getRGB();
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(MathUtil.interpolate(color1.getRed(), color2.getRed(), amount),
                MathUtil.interpolate(color1.getGreen(), color2.getGreen(), amount),
                MathUtil.interpolate(color1.getBlue(), color2.getBlue(), amount),
                MathUtil.interpolate(color1.getAlpha(), color2.getAlpha(), amount));
    }


    public static Color getRainbow(float seconds, float sat, float bright) {
        float hue = (float)(System.currentTimeMillis() % (long)((int)(seconds * 1000.0f))) / (seconds * 1000.0f);
        return Color.getHSBColor(hue, sat, bright);
    }
}
