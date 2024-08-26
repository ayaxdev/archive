package net.jezevcik.argon.module.params;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.system.identifier.Identifiable;
import net.jezevcik.argon.system.identifier.Identifiables;
import net.jezevcik.argon.system.identifier.IdentifierType;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public enum ModuleCategory implements Identifiable {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    WORLD("World"),
    RENDER("Render");

    public final String name;

    ModuleCategory(String name) {
        this.name = name;
    }

    public List<Module> get() {
        return ParekClient.getInstance().modules.getOfCategory(this);
    }

    private final String[] group = new String[] {"category", "module"};

    @Override
    public String getIdentifier(IdentifierType identifierType) {
        return switch (identifierType) {
            case DISPLAY, UNIQUE_SHORT -> name;
            case UNIQUE_NORMAL ->  Identifiables.getIdentifier(this, name);
        };
    }

    @Override
    public String[] getGroup() {
        return group;
    }
}

