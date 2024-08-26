package com.daniel.datsuzei.command;

import com.daniel.datsuzei.feature.Feature;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class CommandTemplate implements Feature {

    public final String name, description, usage;
    public final String[] triggers;

    public abstract boolean run(String[] arguments);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JsonObject serializeFeature() {
        throw new RuntimeException("You cannot serialize a command!");
    }

    @Override
    public void deserializeFeature(JsonObject jsonObject) {
        throw new RuntimeException("You cannot deserialize a command!");
    }

}
