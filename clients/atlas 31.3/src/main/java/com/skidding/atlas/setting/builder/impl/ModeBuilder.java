package com.skidding.atlas.setting.builder.impl;

import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.setting.builder.SettingBuilder;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

@RequiredArgsConstructor
public class ModeBuilder extends SettingBuilder<String> {

    private final String name;
    private final String value;
    public final String[] modes;

    public ModeBuilder(String name, String value, Object[] modes) {
        this.name = name;
        this.value = value;
        this.modes = new String[modes.length];

        for (int i = 0; i < modes.length; i++) {
            this.modes[i] = modes[i].toString();
        }
    }

    @Override
    public SettingFeature<String> build() {
        return new SettingFeature<>(value, name, "Mode", this.dependencies, this.valueChangeListeners, this.valueChangeOverrides) {
            @Override
            public void setValue(String value) {
                if(ArrayUtils.contains(modes, value))
                    super.setValue(value);
            }

            @Override
            public JsonObject serialize() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("selected", getValue());
                return jsonObject;
            }

            @Override
            public void deserialize(JsonObject jsonObject) {
                setValue(jsonObject.get("selected").getAsString());
            }

            @Override
            public SettingBuilder<String> getBuilder() {
                return ModeBuilder.this;
            }
        };
    }

}
