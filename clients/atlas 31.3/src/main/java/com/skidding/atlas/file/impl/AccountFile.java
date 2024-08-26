package com.skidding.atlas.file.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.skidding.atlas.account.AccountFeature;
import com.skidding.atlas.account.AccountManager;
import com.skidding.atlas.file.FileFeature;
import com.skidding.atlas.util.encryption.EncryptionUtil;
import net.optifine.util.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AccountFile extends FileFeature {

    public AccountFile() {
        super("accounts", EncryptionUtil.KeyLevel.MAXIMUM);
    }

    @Override
    public JsonObject serialize() {
        final JsonObject jsonObject = new JsonObject();

        for(final AccountFeature accountFeature : AccountManager.getSingleton().getFeatures()) {
            final JsonObject accountJsonObject = new JsonObject();
            accountJsonObject.addProperty("type", accountFeature.getType());
            accountJsonObject.add("data", accountFeature.serialize());

            jsonObject.add(accountFeature.getName(), accountJsonObject);
        }

        return jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            final JsonObject accountJsonObject = entry.getValue().getAsJsonObject();

            final String type = accountJsonObject.get("type").getAsString();
            final JsonObject data = accountJsonObject.get("data").getAsJsonObject();

            final List<Object> arguments = new ArrayList<>();

            for(Map.Entry<String, JsonElement> dataEntry : data.entrySet()) {
                if(dataEntry.getValue() instanceof JsonPrimitive primitive) {
                    arguments.add(primitive.getAsString());
                }
            }

            AccountManager.getSingleton().addByType(type, arguments.toArray());
        }
    }

}
