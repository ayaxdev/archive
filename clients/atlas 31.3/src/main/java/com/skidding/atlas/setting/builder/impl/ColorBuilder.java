package com.skidding.atlas.setting.builder.impl;

import com.google.gson.JsonObject;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.setting.builder.SettingBuilder;
import lombok.RequiredArgsConstructor;

import java.awt.*;

@RequiredArgsConstructor
public class ColorBuilder extends SettingBuilder<Integer> {

    private final String name;
    private final int red, green, blue, alpha;

    @Override
    public SettingFeature<Integer> build() {
        return new SettingFeature<>(new Color(red, green, blue, alpha).getRGB(), name, "Color", this.dependencies, this.valueChangeListeners, this.valueChangeOverrides) {
            private int red, green, blue, alpha;

            @Override
            public void setValue(Integer value) {
                this.red = value >> 16 & 0xFF;
                this.green = value >> 8 & 0xFF;
                this.blue = value & 0xFF;
                this.alpha = value >> 24 & 0xFF;

                super.setValue((alpha << 24) | (red << 16) | (green << 8) | blue);
            }

            @Override
            public JsonObject serialize() {
                this.red = getValue() >> 16 & 0xFF;
                this.green = getValue() >> 8 & 0xFF;
                this.blue = getValue() & 0xFF;
                this.alpha = getValue() >> 24 & 0xFF;

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("red", red);
                jsonObject.addProperty("green", green);
                jsonObject.addProperty("blue", blue);
                jsonObject.addProperty("alpha", alpha);
                return jsonObject;
            }

            @Override
            public void deserialize(JsonObject jsonObject) {
                int red = jsonObject.get("red").getAsInt();
                int green = jsonObject.get("green").getAsInt();
                int blue = jsonObject.get("blue").getAsInt();
                int alpha = jsonObject.get("alpha").getAsInt();

                setValue((alpha << 24) | (red << 16) | (green << 8) | blue);
            }

            @Override
            public SettingBuilder<Integer> getBuilder() {
                return ColorBuilder.this;
            }
        };
    }

}
