package com.daniel.datsuzei.file.impl;

import com.daniel.datsuzei.feature.Feature;
import com.daniel.datsuzei.feature.Manager;
import com.daniel.datsuzei.file.FileFeature;
import com.daniel.datsuzei.settings.SettingFeature;
import com.daniel.datsuzei.settings.SettingManager;
import com.google.gson.JsonObject;

import java.util.Map;

public class SettingFile extends FileFeature {

    public SettingFile() {
        super("settings");
    }

    @Override
    public JsonObject serializeFeature() {
        JsonObject modulesObject = new JsonObject();

        for(Map.Entry<String, SettingFeature<?>> settingEntry : SettingManager.getSingleton().getMap().entrySet()) {
            modulesObject.add(settingEntry.getKey(), settingEntry.getValue().serializeFeature());
        }

        return modulesObject;
    }

    @Override
    public void deserializeFeature(JsonObject jsonObject) {
        for(Map.Entry<String, SettingFeature<?>> settingEntry : SettingManager.getSingleton().getMap().entrySet()) {
            if(jsonObject.has(settingEntry.getKey())) {
                JsonObject featureObject = jsonObject.getAsJsonObject(settingEntry.getKey());
                if(featureObject != null) {
                    settingEntry.getValue().deserializeFeature(featureObject);
                }
            }
        }
    }

}
