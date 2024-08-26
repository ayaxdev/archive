package net.jezevcik.argon.account.impl;

import de.florianmichael.waybackauthlib.InvalidCredentialsException;
import de.florianmichael.waybackauthlib.WaybackAuthLib;
import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.account.Account;
import net.jezevcik.argon.account.enums.LoginResult;
import net.jezevcik.argon.utils.game.UserUtils;
import net.minecraft.client.session.Session;

import java.net.Proxy;

public class YggdrasilAccount extends Account {

    private final WaybackAuthLib authenticator;

    public final String server, username, pass;

    public YggdrasilAccount(String server, String username, String pass) {
        super(username);

        this.server = server;
        this.username = username;
        this.pass = pass;

        authenticator = new WaybackAuthLib(server);
    }


    @Override
    public LoginResult login() {
        authenticator.setUsername(username);
        authenticator.setPassword(pass);

        try {
            authenticator.logIn();

            UserUtils.login(authenticator.getCurrentProfile().getName(), authenticator.getCurrentProfile().getId(), authenticator.getAccessToken(), Session.AccountType.MOJANG);
        } catch (InvalidCredentialsException e) {
            return LoginResult.INVALID_CREDENTIALS;
        } catch (Exception e) {
            return LoginResult.INTERNAL_ERROR;
        }

        return null;
    }

    @Override
    public void logout() {
        if (!authenticator.isLoggedIn())
            throw new IllegalStateException("Cannot call log-out whole the authenticator is not logged in!");

        try {
            authenticator.logOut();
        } catch (Exception e) {
            ParekClient.LOGGER.error("Failed to log-out {}", name, e);
        }
    }


}
