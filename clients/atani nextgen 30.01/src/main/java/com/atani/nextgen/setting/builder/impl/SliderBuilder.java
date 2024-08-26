package com.atani.nextgen.setting.builder.impl;

import com.atani.nextgen.setting.SettingFeature;
import com.atani.nextgen.setting.builder.SettingBuilder;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SliderBuilder extends SettingBuilder<Float> {

    private final String name;
    private final float value;
    public final float minimum, maximum;
    public final int decimals;

    @Override
    public SettingFeature<Float> build() {
        return new SettingFeature<>(value, name, description, "Slider", this.dependencies, this.valueChangeListeners, this.valueChangeOverrides) {
            @Override
            public void setValue(Float value) {
                value = Math.max(minimum, Math.min(maximum, value));
                super.setValue(value);
            }

            @Override
            public JsonObject serialize() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("value", getValue());
                return jsonObject;
            }

            @Override
            public void deserialize(JsonObject jsonObject) {
                setValue(jsonObject.get("value").getAsFloat());
            }

            @Override
            public SettingBuilder<Float> getBuilder() {
                return SliderBuilder.this;
            }
        };
    }

}
