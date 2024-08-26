package com.daniel.datsuzei.file.impl;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.feature.Feature;
import com.daniel.datsuzei.feature.Manager;
import com.daniel.datsuzei.file.FileFeature;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.*;

public class FeatureFile extends FileFeature {

    private final Manager<?> manager;

    public FeatureFile(String name, Manager<?> manager) {
        super(name);

        this.manager = manager;
    }

    @Override
    public JsonObject serializeFeature() {
        JsonObject modulesObject = new JsonObject();

        for(Feature feature : this.manager.getFeatures()) {
            modulesObject.add(feature.getName(), feature.serializeFeature());
        }

        return modulesObject;
    }

    @Override
    public void deserializeFeature(JsonObject jsonObject) {
        for(Feature feature : this.manager.getFeatures()) {
            if(jsonObject.has(feature.getName())) {
                JsonObject featureObject = jsonObject.getAsJsonObject(feature.getName());
                if(featureObject != null) {
                    feature.deserializeFeature(featureObject);
                }
            }
        }
    }
}
