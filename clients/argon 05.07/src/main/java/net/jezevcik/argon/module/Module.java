package net.jezevcik.argon.module;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.config.interfaces.Configurable;
import net.jezevcik.argon.module.extension.Extension;
import net.jezevcik.argon.module.params.ModuleParams;
import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.system.identifier.Identifiable;
import net.jezevcik.argon.system.identifier.Identifiables;
import net.jezevcik.argon.system.identifier.IdentifierType;
import net.jezevcik.argon.system.minecraft.Minecraft;
import net.jezevcik.argon.system.toggle.RedundantCallException;
import net.jezevcik.argon.system.toggle.Toggleable;

import java.util.ArrayList;
import java.util.List;

public class Module implements Configurable, Identifiable, Toggleable, Minecraft {

    private final List<Extension> extensions = new ArrayList<>();

    public final ModuleParams moduleParams;

    public final Config config;

    private boolean enabled;
    public Integer key;

    public Module(ModuleParams moduleParams) {
        this.moduleParams = moduleParams;
        this.config = new Config(this, "moduleSettings", "Settings");

        this.key = moduleParams.key();
    }

    public void lazyLoad() {
        ParekClient.getInstance().bindManager.addBind(() -> key, this::toggle);
    }

    @Override
    public final void enable() throws RedundantCallException {
        if (enabled)
            throw new RedundantCallException(this, true);

        enabled = true;

        onToggle(true);

        onEnable();

        extensions.forEach(Extension::onModuleEnable);

        onToggle(false);

        ParekClient.getInstance().eventBus.subscribe(this);
    }

    @Override
    public final void disable() throws RedundantCallException {
        if (!enabled)
            throw new RedundantCallException(this, false);

        enabled = false;

        ParekClient.getInstance().eventBus.unsubscribe(this);

        onToggle(false);

        extensions.forEach(Extension::onModuleDisable);

        onDisable();

        onToggle(true);
    }

    @Override
    public final boolean isEnabled() {
        return enabled;
    }

    protected void onEnable() { }

    protected void onDisable() { }

    protected void onToggle(boolean pre) { }

    @Override
    public final Config getConfig() {
        return config;
    }

    @Override
    public final String getIdentifier(IdentifierType identifierType) {
        return switch (identifierType) {
            case UNIQUE_SHORT -> moduleParams.name();
            case UNIQUE_NORMAL -> Identifiables.getIdentifier(this, moduleParams.name());
            case DISPLAY -> moduleParams.displayName();
        };
    }

    private final String[] group = new String[] {"module"};

    @Override
    public final String[] getGroup() {
        return group;
    }

}
