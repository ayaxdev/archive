package com.skidding.atlas.feature;

import com.skidding.atlas.util.minecraft.IGameSettings;
import com.skidding.atlas.util.minecraft.IMinecraft;
import com.google.gson.JsonObject;
import com.skidding.atlas.util.minecraft.INetwork;
import com.skidding.atlas.util.minecraft.IPlayer;

public interface Feature extends IMinecraft, IPlayer, INetwork, IGameSettings {

    String getName();

    default String getDescription() {
        return null;
    }

    JsonObject serialize();

    void deserialize(JsonObject jsonObject);

}
