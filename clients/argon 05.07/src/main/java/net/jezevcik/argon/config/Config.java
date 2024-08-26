package net.jezevcik.argon.config;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.ImmutableList;
import net.jezevcik.argon.config.interfaces.ConfigEntry;
import net.jezevcik.argon.config.setting.Setting;
import net.jezevcik.argon.file.interfaces.Savable;
import net.jezevcik.argon.system.identifier.Identifiable;
import net.jezevcik.argon.system.identifier.Identifiables;
import net.jezevcik.argon.system.identifier.IdentifierType;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Config implements ConfigEntry, Savable {

    private final List<ConfigEntry> list = new ArrayList<>();
    public List<ConfigEntry> publicList = ImmutableList.of();
    public Supplier<Boolean> visibility = () -> true;

    public boolean enabled = true, hidden = false;

    public final String[] identifierGroup;
    public final String name, displayName;
    public final Config config;

    public Config(final Identifiable parent, final String name, final Config config) {
        this(parent, name, name, config);
    }

    public Config(final Identifiable parent, final String name, final String displayName, final Config config) {
        this.identifierGroup = ArrayUtils.add(parent.getGroup(), "config");
        this.name = name;
        this.displayName = displayName;

        if ((this.config = config) != null)
            config.add(this);
    }

    public Config(final Identifiable parent, final String name, final String displayName) {
        this(parent, name, displayName, null);
    }

    public final void add(final ConfigEntry configEntry) {
        this.list.add(configEntry);
        this.publicList = ImmutableList.copyOf(list);
    }

    public final List<ConfigEntry> getListWithSubs() {
        final List<ConfigEntry> returnList = new ArrayList<>();

        for (ConfigEntry configEntry : this.publicList) {
            if (configEntry instanceof Config config) {
                if (!config.visible())
                    continue;

                returnList.add(config);
                returnList.addAll(config.getListWithSubs());
            } else {
                returnList.add(configEntry);
            }
        }

        return returnList;
    }

    @Override
    public boolean visible() {
        return visibility.get();
    }

    @Override
    public String getIdentifier(IdentifierType identifierType) {
        return switch (identifierType) {
            case UNIQUE_SHORT -> name;
            case UNIQUE_NORMAL -> Identifiables.getIdentifier(this, name);
            case DISPLAY -> displayName;
        };
    }

    @Override
    public String[] getGroup() {
        return identifierGroup;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public JSONObject getData() {
        final JSONObject configObject = new JSONObject();

        for (ConfigEntry configEntry : publicList) {
            if (!(configEntry instanceof Savable savable))
                continue;

            configObject.put(configEntry.getIdentifier(IdentifierType.UNIQUE_NORMAL), savable.getData());
        }

        return configObject;
    }

    @Override
    public void setData(JSONObject object) {
        for (ConfigEntry configEntry : publicList) {
            final String settingIdentifier = configEntry.getIdentifier(IdentifierType.UNIQUE_NORMAL);

            if (!(configEntry instanceof Savable savable))
                continue;

            if (!object.containsKey(settingIdentifier))
                continue;

            final JSONObject settingObject = object.getJSONObject(settingIdentifier);

            savable.setData(settingObject);
        }
    }
}
