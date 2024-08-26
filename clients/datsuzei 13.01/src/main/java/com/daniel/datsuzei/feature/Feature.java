package com.daniel.datsuzei.feature;

import com.daniel.datsuzei.util.interfaces.MinecraftClient;
import com.google.gson.JsonObject;

public interface Feature extends MinecraftClient {
    String getName();

    JsonObject serializeFeature();

    void deserializeFeature(JsonObject jsonObject);

}