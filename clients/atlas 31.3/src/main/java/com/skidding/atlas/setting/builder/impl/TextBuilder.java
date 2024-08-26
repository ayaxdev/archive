package com.skidding.atlas.setting.builder.impl;

import com.google.gson.JsonObject;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.setting.builder.SettingBuilder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TextBuilder extends SettingBuilder<String> {

    private final String name;
    private final String value;

    @Override
    public SettingFeature<String> build() {
        return new SettingFeature<>(value, name, "Text", this.dependencies, this.valueChangeListeners, this.valueChangeOverrides) {
            @Override
            public JsonObject serialize() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("value", getValue());
                return jsonObject;
            }

            @Override
            public void deserialize(JsonObject jsonObject) {
                setValue(jsonObject.get("value").getAsString());
            }

            @Override
            public SettingBuilder<String> getBuilder() {
                return TextBuilder.this;
            }
        };
    }

}
