package com.skidding.atlas.setting.builder.impl;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.font.FontManager;
import com.skidding.atlas.font.FontRendererValue;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.setting.builder.SettingBuilder;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.FontRenderer;

import java.io.File;

@RequiredArgsConstructor
public class FontBuilder extends SettingBuilder<FontRendererValue> {

    private final String name;
    private final String family, fontType;
    private final float size;

    @Override
    public SettingFeature<FontRendererValue> build() {
        return new SettingFeature<>(new FontRendererValue(family, fontType, size, FontManager.getSingleton().get(family, fontType, size)), name, "Font", this.dependencies, this.valueChangeListeners, this.valueChangeOverrides) {

            @Override
            public SettingBuilder<FontRendererValue> getBuilder() {
                return FontBuilder.this;
            }

            @Override
            public JsonObject serialize() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("Family", getValue().family());
                jsonObject.addProperty("Type", getValue().fontType());
                jsonObject.addProperty("Size", getValue().size());
                return jsonObject;
            }

            @Override
            public void deserialize(JsonObject jsonObject) {
                if (jsonObject.has("Family") && jsonObject.has("Type") && jsonObject.has("Size")) {
                    String family = jsonObject.get("Family").getAsString();
                    String type = jsonObject.get("Type").getAsString();
                    float size = jsonObject.get("Size").getAsFloat();

                    try {
                        final FontRenderer fontRenderer = FontManager.getSingleton().get(family, type, size);
                        if(fontRenderer != null) {
                            setValue(new FontRendererValue(family, type, size, FontManager.getSingleton().get(family, type, size)));
                        } else {
                            AtlasClient.getInstance().logger.error("Could not load saved font value");
                        }
                    } catch (Exception e) {
                        AtlasClient.getInstance().logger.error(STR."Could not load saved font value @ \{getName()}", e);
                    }
                }
            }
        };
    }
}
