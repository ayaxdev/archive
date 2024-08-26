package com.skidding.atlas.file.impl;

import com.google.gson.JsonObject;
import com.skidding.atlas.feature.Feature;
import com.skidding.atlas.feature.Manager;
import com.skidding.atlas.file.FileFeature;
import com.skidding.atlas.util.encryption.EncryptionUtil;

public class FeatureFile extends FileFeature {

    private final Manager<?> manager;

    public FeatureFile(String name, Manager<?> manager, EncryptionUtil.KeyLevel keyLevel) {
        super(name, keyLevel);

        this.manager = manager;
    }

    @Override
    public JsonObject serialize() {
        JsonObject moduleObject = new JsonObject();

        for(Object object : this.manager.getFeatures()) {
            if(object instanceof Feature feature)
                moduleObject.add(feature.getName(), feature.serialize());
        }

        return moduleObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        for(Object object : this.manager.getFeatures()) {
            if (object instanceof Feature feature) {
                if (jsonObject.has(feature.getName())) {
                    JsonObject featureObject = jsonObject.getAsJsonObject(feature.getName());
                    if (featureObject != null) {
                        feature.deserialize(featureObject);
                    }
                }
            }
        }
    }
}