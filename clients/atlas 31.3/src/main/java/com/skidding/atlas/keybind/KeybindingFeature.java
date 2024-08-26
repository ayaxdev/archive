package com.skidding.atlas.keybind;

import com.skidding.atlas.feature.Feature;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class KeybindingFeature implements Feature {

    public final String name;
    public final String description;
    public final Consumer<Integer> setter;

    public final List<Integer> allowed = new ArrayList<>(),
            disallowed = new ArrayList<>();

    public int key;
    public boolean binding;

    public KeybindingFeature(String name, String description) {
        this(name, description, null);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public JsonObject serialize() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("key", key);

        JsonArray allowed = new JsonArray();
        for(int allowedKey : this.allowed) {
            allowed.add(allowedKey);
        }

        JsonArray disallowed = new JsonArray();
        for(int disallowedKey : this.disallowed) {
            disallowed.add(disallowedKey);
        }

        jsonObject.add("allowed", allowed);
        jsonObject.add("disallowed", disallowed);

        return jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        if(jsonObject.has("key"))
            key = jsonObject.get("key").getAsInt();
        if(jsonObject.has("allowed")) {
            JsonArray jsonArray = (JsonArray) jsonObject.get("allowed");
            for(JsonElement jsonElement : jsonArray.asList()) {
                allowed.add(jsonElement.getAsInt());
            }
        }
        if(jsonObject.has("disallowed")) {
            JsonArray jsonArray = (JsonArray) jsonObject.get("disallowed");
            for(JsonElement jsonElement : jsonArray.asList()) {
                allowed.add(jsonElement.getAsInt());
            }
        }
    }
}
