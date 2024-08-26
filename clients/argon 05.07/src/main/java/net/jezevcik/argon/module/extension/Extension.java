package net.jezevcik.argon.module.extension;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.system.identifier.Identifiable;
import net.jezevcik.argon.system.identifier.Identifiables;
import net.jezevcik.argon.system.identifier.IdentifierType;
import net.jezevcik.argon.system.minecraft.Minecraft;

public class Extension implements Identifiable, Minecraft {

    public final String name;

    public final boolean boundToModule;

    public final Config config;

    public Config parent;

    public Extension(final String name, final Config parent, final boolean boundToModule) {
        this.name = name;
        this.parent = parent;
        this.boundToModule = boundToModule;

        this.config = new Config(parent, name, parent);
        this.config.hidden = true;
    }

    public void onModuleEnable() {
        if (boundToModule) {
            enable();
        }
    }

    public void onModuleDisable() {
        if (boundToModule) {
            disable();
        }
    }

    public void enable() {
        onEnable();
        ParekClient.getInstance().eventBus.subscribe(this);
    }

    public void disable() {
        ParekClient.getInstance().eventBus.unsubscribe(this);
        onDisable();
    }

    protected void onEnable() { }

    protected void onDisable() { }

    @Override
    public final String getIdentifier(IdentifierType identifierType) {
        return switch (identifierType) {
            case UNIQUE_SHORT, DISPLAY -> name;
            case UNIQUE_NORMAL -> Identifiables.getIdentifier(this, name);
        };
    }

    private final String[] group = new String[] {"module"};

    @Override
    public final String[] getGroup() {
        return group;
    }
}
