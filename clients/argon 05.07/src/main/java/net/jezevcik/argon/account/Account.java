package net.jezevcik.argon.account;

import net.jezevcik.argon.account.enums.LoginResult;
import net.jezevcik.argon.system.identifier.Identifiable;
import net.jezevcik.argon.system.identifier.Identifiables;
import net.jezevcik.argon.system.identifier.IdentifierType;
import net.jezevcik.argon.system.minecraft.Minecraft;

public abstract class Account implements Identifiable, Minecraft {

    public final String name;

    public Account(String name) {
        this.name = name;
    }

    public abstract LoginResult login();

    public abstract void logout();

    @Override
    public final String getIdentifier(IdentifierType identifierType) {
        return switch (identifierType) {
            case UNIQUE_SHORT, DISPLAY -> name;
            case UNIQUE_NORMAL -> Identifiables.getIdentifier(this, name);
        };
    }

    private final String[] group = new String[] {"account"};

    @Override
    public final String[] getGroup() {
        return group;
    }

}
