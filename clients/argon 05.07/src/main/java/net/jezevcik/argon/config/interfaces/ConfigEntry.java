package net.jezevcik.argon.config.interfaces;

import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.system.identifier.Identifiable;
import net.jezevcik.argon.system.identifier.IdentifierType;

import java.util.ArrayList;
import java.util.List;
import java.util.SequencedSet;

public interface ConfigEntry extends Identifiable {

    Config getConfig();

    default List<Config> getParents(boolean hidden) {
        final List<Config> configs = new ArrayList<>();

        if (getConfig() != null && getConfig().getConfig() != null) {
            if (hidden || !getConfig().hidden)
                configs.add(getConfig().getConfig());

            configs.addAll(getConfig().getParents(hidden));
        }

        return configs;
    }

    default boolean visible() {
        return true;
    }

}
