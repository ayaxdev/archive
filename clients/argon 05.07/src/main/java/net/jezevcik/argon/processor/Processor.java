package net.jezevcik.argon.processor;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.system.identifier.Identifiable;
import net.jezevcik.argon.system.identifier.Identifiables;
import net.jezevcik.argon.system.identifier.IdentifierType;
import net.jezevcik.argon.system.minecraft.Minecraft;

public class Processor implements Identifiable, Minecraft {
    
    public final String name;

    public Processor(String name) {
        this.name = name;
    }

    public void init() {
        ParekClient.getInstance().eventBus.subscribe(this);
    }

    @Override
    public String getIdentifier(IdentifierType identifierType) {
        return switch (identifierType) {
            case UNIQUE_SHORT, DISPLAY -> name;
            case UNIQUE_NORMAL -> Identifiables.getIdentifier(this, name);
        };
    }

    private final String[] group = new String[] {"processor"};

    @Override
    public String[] getGroup() {
        return group;
    }
}
