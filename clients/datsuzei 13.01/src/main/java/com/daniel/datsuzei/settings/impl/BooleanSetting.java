package com.daniel.datsuzei.settings.impl;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.settings.SettingFeature;
import com.daniel.datsuzei.util.json.DeserializationUtil;
import com.google.gson.JsonObject;

public class BooleanSetting extends SettingFeature<Boolean> {

    private boolean value;

    public BooleanSetting(String name, boolean value) {
        super(name);
        this.value = value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }


    @Override
    public JsonObject serializeFeature() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("enabled", value);
        return jsonObject;
    }

    @Override
    public void deserializeFeature(JsonObject jsonObject) {
        try {
            setValue(DeserializationUtil.elementExists("enabled", jsonObject).getAsBoolean());
        } catch (Exception e) {
            DatsuzeiClient.getSingleton().getLogger().error(STR."Failed to deserialize the boolean setting \{ getName()}:", e);
        }
    }
}
