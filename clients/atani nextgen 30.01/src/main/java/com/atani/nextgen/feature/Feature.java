package com.atani.nextgen.feature;

import com.atani.nextgen.util.minecraft.MinecraftClient;
import com.google.gson.JsonObject;

public interface Feature extends MinecraftClient {

    String getName();

    String getDescription();

    JsonObject serialize();

    void deserialize(JsonObject jsonObject);

}
