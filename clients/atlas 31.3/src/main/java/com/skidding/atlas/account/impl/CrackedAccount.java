package com.skidding.atlas.account.impl;

import com.google.gson.JsonObject;
import com.skidding.atlas.account.AccountFeature;
import com.skidding.atlas.account.util.LoginResult;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Session;

@RequiredArgsConstructor
public class CrackedAccount extends AccountFeature {

    public final String name;

    @Override
    public LoginResult login() {
        return new LoginResult(new Session(name, "", "", "mojang"), LoginResult.Result.SUCCESS, null);
    }

    @Override
    public String getType() {
        return "Cracked";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JsonObject serialize() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        return jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {

    }
}
