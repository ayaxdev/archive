package com.daniel.datsuzei.settings.impl;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.settings.SettingFeature;
import com.daniel.datsuzei.util.json.DeserializationUtil;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class ModeSetting extends SettingFeature<String> {

    private String value;
    private final List<String> modes;

    public ModeSetting(String name, String value, String... modes) {
        super(name);

        this.value = value;
        this.modes = Arrays.asList(modes);
    }

    @Override
    public void setValue(String value) {
        if(modes.contains(value))
            this.value = value;
        else
            throw new IllegalArgumentException(STR."Value \{value} is not in the mode list \{modes.toArray()}");
    }

    @Override
    public JsonObject serializeFeature() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mode", value);
        return jsonObject;
    }

    @Override
    public void deserializeFeature(JsonObject jsonObject) {
        try {
            setValue(DeserializationUtil.elementExists("mode", jsonObject).getAsString());
        } catch (Exception e) {
            DatsuzeiClient.getSingleton().getLogger().error(STR."Failed to deserialize the mode setting \{ getName()}:", e);
        }
    }
}
