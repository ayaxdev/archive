package net.jezevcik.argon.account.impl;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.jezevcik.argon.account.Account;
import net.jezevcik.argon.account.enums.LoginResult;
import net.jezevcik.argon.mixin.MinecraftClientAccessor;
import net.jezevcik.argon.utils.game.UserUtils;
import net.minecraft.client.session.Session;

import java.util.Optional;
import java.util.UUID;

public class OpenAuthAccount extends Account {

    public final String mail, pass;

    public OpenAuthAccount(String mail, String pass) {
        super(mail);

        this.mail = mail;
        this.pass = pass;
    }

    @Override
    public LoginResult login() {
        try {
            final MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();

            final MicrosoftAuthResult result = authenticator.loginWithCredentials(mail, pass);

            UserUtils.login(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), Session.AccountType.MSA);

            return LoginResult.SUCCESS;
        } catch (Exception e) {
            if (e.getMessage().equals("Invalid credentials or tokens"))
                return LoginResult.INVALID_CREDENTIALS;
            else
                return LoginResult.INTERNAL_ERROR;
        }
    }

    @Override
    public void logout() {
        // Nothing required
    }


}
