package com.skidding.atlas.account;

import com.skidding.atlas.account.impl.CrackedAccount;
import com.skidding.atlas.account.impl.OpenAuthAccount;
import com.skidding.atlas.feature.Manager;

public final class AccountManager extends Manager<AccountFeature> {

    private static volatile AccountManager accountManager;

    public static synchronized AccountManager getSingleton() {
        return accountManager == null ? accountManager = new AccountManager() : accountManager;
    }

    public void addByType(String type, Object... args) {
        final AccountFeature accountFeature = switch (type) {
            case "Cracked" -> new CrackedAccount((String) args[0]);
            case "OpenAuth" -> new OpenAuthAccount((String) args[0], (String) args[1]);
            default -> throw new UnsupportedOperationException(STR."Unsupported account type \{type}");
        };

        this.map.put(STR."\{accountFeature.getType()}:\{accountFeature.getName()}", accountFeature);
    }

}
