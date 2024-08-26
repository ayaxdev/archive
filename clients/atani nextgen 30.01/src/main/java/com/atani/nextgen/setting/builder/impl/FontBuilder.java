package com.atani.nextgen.setting.builder.impl;

import com.atani.nextgen.font.FontManager;
import com.atani.nextgen.setting.SettingFeature;
import com.atani.nextgen.setting.builder.SettingBuilder;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.FontRenderer;

@RequiredArgsConstructor
public class FontBuilder extends SettingBuilder<FontRenderer> {

    private final String name;
    private final String family, type;
    private final float size;

    @Override
    public SettingFeature<FontRenderer> build() {
        return new SettingFeature<FontRenderer>(FontManager.getSingleton().get(family, type, size), name, description, "Font", this.dependencies, this.valueChangeListeners, this.valueChangeOverrides) {
            @Override
            public SettingBuilder<FontRenderer> getBuilder() {
                return FontBuilder.this;
            }

            @Override
            public JsonObject serialize() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("Family", family);
                jsonObject.addProperty("Type", type);
                jsonObject.addProperty("Size", size);
                return jsonObject;
            }

            @Override
            public void deserialize(JsonObject jsonObject) {
                if(jsonObject.has("Family") && jsonObject.has("Type") && jsonObject.has("Size")) {
                    String family = jsonObject.get("Family").getAsString();
                    String type = jsonObject.get("Type").getAsString();
                    float size = jsonObject.get("Size").getAsFloat();

                    setValue(FontManager.getSingleton().get(family, type, size));
                }
            }
        };
    }
}
