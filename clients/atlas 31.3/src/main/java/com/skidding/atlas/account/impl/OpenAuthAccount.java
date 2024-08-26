package com.skidding.atlas.account.impl;

import com.google.gson.JsonObject;
import com.skidding.atlas.account.AccountFeature;
import com.skidding.atlas.account.util.LoginResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Session;

@RequiredArgsConstructor
public class OpenAuthAccount extends AccountFeature {

    public final String email, password;

    @Override
    public LoginResult login() {
        final MicrosoftAuthenticator microsoftAuthenticator = new MicrosoftAuthenticator();
        try {
            final MicrosoftAuthResult microsoftAuthResult = microsoftAuthenticator.loginWithCredentials(email, password);
            final MinecraftProfile minecraftProfile = microsoftAuthResult.getProfile();
            return new LoginResult(new Session(minecraftProfile.getName(), minecraftProfile.getId(), microsoftAuthResult.getAccessToken(), "mojang"),
                    LoginResult.Result.SUCCESS, null);
        } catch (MicrosoftAuthenticationException e) {
            return new LoginResult(null, LoginResult.Result.INVALID_ARGUMENTS, e);
        }
    }

    @Override
    public String getType() {
        return "OpenAuth";
    }

    @Override
    public String getName() {
        return STR."\{email}<--:-->\{password}";
    }

    @Override
    public JsonObject serialize() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", this.email);
        jsonObject.addProperty("pass", this.password);
        return jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) { }
}
