package com.skidding.atlas.account;

import com.skidding.atlas.account.util.LoginResult;
import com.skidding.atlas.feature.Feature;

public abstract class AccountFeature implements Feature {

    public abstract LoginResult login();

    public abstract String getType();

}
