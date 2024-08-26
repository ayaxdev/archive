package com.skidding.atlas.file.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.skidding.atlas.file.FileFeature;
import com.skidding.atlas.processor.ProcessorManager;
import com.skidding.atlas.processor.impl.PasswordProcessor;
import com.skidding.atlas.util.encryption.EncryptionUtil;
import net.optifine.util.Json;

import java.util.LinkedHashMap;
import java.util.Map;

public class PasswordFile extends FileFeature {

    private final PasswordProcessor processor = ProcessorManager.getSingleton().getByClass(PasswordProcessor.class);

    public PasswordFile() {
        super("passwords", EncryptionUtil.KeyLevel.MAXIMUM);
    }

    @Override
    public JsonObject serialize() {
        final JsonObject jsonObject = new JsonObject();

        for(Map.Entry<String, Map<String, String>> entry : processor.savedPasswordsMap.entrySet()) {
            JsonObject serverJsonObject = new JsonObject();

            for(Map.Entry<String, String> userEntry : entry.getValue().entrySet()) {
                final JsonObject userJsonObject = new JsonObject();
                userJsonObject.addProperty("password", userEntry.getValue());
                serverJsonObject.add(userEntry.getKey(), userJsonObject);
           }

            jsonObject.add(entry.getKey(), serverJsonObject);
        }

        return jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        final Map<String, Map<String, String>> loaded = new LinkedHashMap<>();

        for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if(entry.getValue().isJsonObject()) {
                final JsonObject serverJsonObject = entry.getValue().getAsJsonObject();
                final Map<String, String> users = new LinkedHashMap<>();

                for(Map.Entry<String, JsonElement> userEntry : serverJsonObject.entrySet()) {
                    if(userEntry.getValue().isJsonObject()) {
                        final JsonObject userJsonObject = userEntry.getValue().getAsJsonObject();

                        if (userJsonObject.has("password") && userJsonObject.get("password").isJsonPrimitive()) {
                            final JsonPrimitive password = userJsonObject.get("password").getAsJsonPrimitive();
                            users.put(userEntry.getKey(), password.getAsString());
                        }
                    }
                }

                loaded.put(entry.getKey(), users);
            }
        }

        processor.savedPasswordsMap.putAll(loaded);
    }

}
