package com.skidding.atlas.file.impl;

import com.google.gson.JsonObject;
import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.file.FileFeature;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.setting.SettingManager;
import com.skidding.atlas.util.encryption.EncryptionUtil;

import java.util.Map;

public class SettingFile extends FileFeature {

    public SettingFile() {
        super("settings", EncryptionUtil.KeyLevel.MEDIUM);
    }

    @Override
    public JsonObject serialize() {
        JsonObject modulesObject = new JsonObject();

        for(Map.Entry<String, SettingFeature<?>> settingEntry : SettingManager.getSingleton().getMap().entrySet()) {
            modulesObject.add(settingEntry.getKey(), settingEntry.getValue().serialize());
        }

        return modulesObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        for(Map.Entry<String, SettingFeature<?>> settingEntry : SettingManager.getSingleton().getMap().entrySet()) {
            if(jsonObject.has(settingEntry.getKey())) {
                JsonObject featureObject = jsonObject.getAsJsonObject(settingEntry.getKey());
                if(featureObject != null) {
                    try {
                        settingEntry.getValue().deserialize(featureObject);
                    } catch (Exception e) {
                        AtlasClient.getInstance().logger.error(STR. "Failed to deserialize setting \{settingEntry.getKey()}:", e);
                    }
                }
            }
        }
    }

}