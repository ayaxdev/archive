package ja.tabio.argon.setting.impl;

import ja.tabio.argon.setting.Setting;

import java.awt.*;
import java.util.Objects;

public class ColorSetting extends Setting<ColorSetting.ColorData> {

    private ColorData value;

    public ColorSetting(String name, String displayName, int red, int green, int blue, int alpha) {
        super(name, displayName);

        this.value = new ColorData(red, green, blue, alpha);
    }

    public ColorSetting(String name, int red, int green, int blue, int alpha) {
        this(name, name, red, green, blue, alpha);
    }

    public ColorSetting(String name, String displayName, int red, int green, int blue) {
        this(name, displayName, red, green, blue, 255);
    }

    public ColorSetting(String name, int red, int green, int blue) {
        this(name, name, red, green, blue);
    }

    public ColorSetting(String name, String displayName, Color color) {
        super(name, displayName);

        this.value = new ColorData(color);
    }

    public ColorSetting(String name, Color color) {
        this(name, name, color);
    }

    public ColorSetting(String name, String displayName, ColorData color) {
        super(name, displayName);

        this.value = color;
    }

    public ColorSetting(String name, ColorData color) {
        this(name, name, color);
    }

    public Color getColor() {
        return this.value.toColor();
    }

    public int getRGB() {
        return this.value.toRGB();
    }

    @Override
    public ColorData getValue() {
        return value;
    }

    @Override
    public void setValue(ColorData value) {
        if (Objects.equals(this.value, value))
            return;

        final ColorData oldValue = this.value;
        this.changeListener.onChange(true, oldValue, value);

        this.value = value;

        this.changeListener.onChange(false, oldValue, value);
    }

    public record ColorData(int red, int green, int blue, int alpha) {

        public ColorData(Color color) {
            this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

            validate();
        }

        public ColorData(int rgb) {
             this((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, (rgb >> 0) & 0xFF, (rgb >> 24) & 0xff);
        }

        public ColorData(float red, float green, float blue, float alpha) {
            this((int) (red * 255),
                    (int) (green * 255),
                    (int) (blue * 255),
                    (int) (alpha * 255));

            validate();
        }

        public Color toColor() {
            validate();

            return new Color(red, green, blue, alpha);
        }

        public int toRGB() {
            validate();

            return ((alpha & 0xFF) << 24) |
                    ((red & 0xFF) << 16) |
                    ((green & 0xFF) << 8)  |
                    ((blue & 0xFF));
        }

        private void validate() {
            if ((red < 0 || red > 255) ||
                    (green < 0 || green > 255) ||
                    (blue < 0 || blue > 255) ||
                    (alpha < 0 || alpha > 255))
                throw new IllegalStateException("Invalid color data!");
        }

    }

}
