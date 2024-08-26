package com.skidding.atlas.setting.interfaces;

import com.skidding.atlas.setting.builder.impl.*;

public interface Settings {

    default CheckBuilder check(String name, boolean value) {
        return new CheckBuilder(name, value);
    }

    default FontBuilder font(String name, String family, String type, float size) {
        return new FontBuilder(name, family, type, size);
    }

    default ModeBuilder mode(String name, String value, Object[] modes) {
        return new ModeBuilder(name, value, modes);
    }

    default SliderBuilder slider(String name, float value, float minimum, float maximum, int decimals) {
        return new SliderBuilder(name, value, minimum, maximum, decimals);
    }

    default ColorBuilder color(String name, int red, int green, int blue, int alpha) {
        return new ColorBuilder(name, red, green, blue, alpha);
    }

    default TextBuilder text(String name, String value) {
        return new TextBuilder(name, value);
    }

}
