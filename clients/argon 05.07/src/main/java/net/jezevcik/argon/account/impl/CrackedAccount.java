package net.jezevcik.argon.account.impl;

import net.jezevcik.argon.account.Account;
import net.jezevcik.argon.account.enums.LoginResult;
import net.jezevcik.argon.utils.game.UserUtils;

public class CrackedAccount extends Account {

    public CrackedAccount(String name) {
        super(name);
    }

    @Override
    public LoginResult login() {
        UserUtils.login(name);
        return LoginResult.SUCCESS;
    }

    @Override
    public void logout() {
        // Nothing required
    }
}
