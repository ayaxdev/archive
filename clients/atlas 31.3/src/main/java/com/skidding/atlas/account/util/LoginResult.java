package com.skidding.atlas.account.util;

import net.minecraft.util.Session;

public record LoginResult(Session session, Result result, Exception cause) {

    public enum Result {
        SUCCESS, INVALID_ARGUMENTS, INTERNAL_ERROR
    }

}
