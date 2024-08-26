package com.daniel.datsuzei.settings.impl;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.settings.SettingFeature;
import com.daniel.datsuzei.util.json.DeserializationUtil;
import com.daniel.datsuzei.util.math.MathUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

@Getter
public class NumberSetting<T extends Number> extends SettingFeature<T> {

    private T value;
    private final T minimum, maximum;

    public NumberSetting(String name, T value, T minimum, T maximum) {
        super(name);

        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;

        if(!(this.value instanceof Float || this.value instanceof Integer))
            throw new IllegalArgumentException("Value must be either an integer or a float!");
    }

    @Override
    public void setValue(T value) {
        float floatValue = value.floatValue();
        float newValue = MathUtil.clamp(floatValue, minimum.floatValue(), maximum.floatValue());

        if(this.value instanceof Integer) {
            this.value = (T) (Number) Math.round(newValue);
        } else {
            this.value = (T) (Number) newValue;
        }
    }

    @Override
    public JsonObject serializeFeature() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", String.valueOf(value instanceof Integer ? value.intValue() : value.floatValue()));
        return jsonObject;
    }

    @Override
    public void deserializeFeature(JsonObject jsonObject) {
        try {
            final JsonElement valueElement = DeserializationUtil.elementExists("value", jsonObject);
            setValue((T) (Object) (minimum instanceof Integer ? (int) valueElement.getAsFloat() : valueElement.getAsFloat()));
        } catch (Exception e) {
            DatsuzeiClient.getSingleton().getLogger().error(STR."Failed to deserialize the number setting \{ getName()}:", e);
        }
    }
}
