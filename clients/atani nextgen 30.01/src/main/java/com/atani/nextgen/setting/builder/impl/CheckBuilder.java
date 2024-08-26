package com.atani.nextgen.setting.builder.impl;

import com.atani.nextgen.setting.SettingFeature;
import com.atani.nextgen.setting.builder.SettingBuilder;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CheckBuilder extends SettingBuilder<Boolean> {

    private final String name;
    private final boolean value;

    @Override
    public SettingFeature<Boolean> build() {
        return new SettingFeature<>(value, name, description, "Checkbox", this.dependencies, this.valueChangeListeners, this.valueChangeOverrides) {
            @Override
            public JsonObject serialize() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("enabled", getValue());
                return jsonObject;
            }

            @Override
            public void deserialize(JsonObject jsonObject) {
                setValue(jsonObject.get("enabled").getAsBoolean());
            }

            @Override
            public SettingBuilder<Boolean> getBuilder() {
                return CheckBuilder.this;
            }
        };
    }

}
