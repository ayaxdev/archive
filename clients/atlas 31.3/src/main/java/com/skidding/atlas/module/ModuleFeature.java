package com.skidding.atlas.module;

import com.google.gson.JsonObject;
import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.feature.Feature;
import com.skidding.atlas.setting.interfaces.Settings;
import com.skidding.atlas.util.string.StringUtil;
import de.florianmichael.rclasses.common.array.ArrayUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public abstract class ModuleFeature implements Feature, Settings {

    public final String name, description;
    public final ModuleCategory moduleCategory;
    private final boolean useEvents;

    public boolean saveEnabled, saveToConfig, loadFromConfig;

    @Getter
    private boolean enabled;

    // Components are objects that get registered alongside the module
    protected List<Object> components;

    // Will be handled by KeybindingManager
    public final int defaultKey;
    public final int[] allowedKeys, disallowedKeys;

    protected ModuleFeature(ModuleBuilder moduleBuilder) {
        this.name = moduleBuilder.name;
        this.description = moduleBuilder.description;
        this.moduleCategory = moduleBuilder.moduleCategory;

        this.useEvents = moduleBuilder.useEvents;

        this.saveEnabled = moduleBuilder.saveEnabled;
        this.saveToConfig = moduleBuilder.saveToConfig;
        this.loadFromConfig = moduleBuilder.loadFromConfig;

        this.enabled = moduleBuilder.enabledByDefault;

        this.components = moduleBuilder.components;

        this.defaultKey = moduleBuilder.key;
        this.allowedKeys = moduleBuilder.allowedKeys;
        this.disallowedKeys = moduleBuilder.disallowedKeys;
    }

    public void toggleEnabled() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            onEnable();
            if (useEvents) {
                AtlasClient.getInstance().eventPubSub.subscribe(this);
                this.components.forEach(object -> AtlasClient.getInstance().eventPubSub.subscribe(object));
            }
        } else {
            if (useEvents) {
                this.components.forEach(object -> AtlasClient.getInstance().eventPubSub.unsubscribe(object));
                AtlasClient.getInstance().eventPubSub.unsubscribe(this);
            }
            onDisable();
        }
    }

    protected abstract void onEnable();

    protected abstract void onDisable();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public JsonObject serialize() {
        if (this.saveToConfig) {
            final JsonObject jsonObject = new JsonObject();

            if (this.saveEnabled)
                jsonObject.addProperty("enabled", enabled);
            jsonObject.addProperty("saveToConfig", saveToConfig);
            jsonObject.addProperty("loadFromConfig", loadFromConfig);

            return jsonObject;
        }

        return null;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        if (this.loadFromConfig) {
            if (jsonObject.has("enabled"))
                setEnabled(jsonObject.get("enabled").getAsBoolean());
            if (jsonObject.has("saveToConfig"))
                saveToConfig = jsonObject.get("saveToConfig").getAsBoolean();
            if (jsonObject.has("loadFromConfig"))
                loadFromConfig = jsonObject.get("loadFromConfig").getAsBoolean();
        }
    }

    @SuppressWarnings("unused")
    public static class ModuleBuilder {
        private final String name, description;
        private final ModuleCategory moduleCategory;
        private boolean enabledByDefault;

        private boolean useEvents = true,
                saveEnabled = true,
                saveToConfig = true,
                loadFromConfig = true;

        private final List<Object> components = new ArrayList<>();

        private int key = 0;
        private int[] allowedKeys = null, disallowedKeys = null;

        public ModuleBuilder(String name, String description, ModuleCategory moduleCategory) {
            if (name == null || description == null || moduleCategory == null)
                throw new IllegalArgumentException("Name, description and category must not be null");

            if (StringUtil.DOT_REGEX_PATTERN.matcher(description).find(0))
                throw new RuntimeException("GO KILL YOURSELF");

            this.name = name;
            this.description = description;
            this.moduleCategory = moduleCategory;
        }

        public ModuleBuilder withEvents(boolean useEvents) {
            this.useEvents = useEvents;
            return this;
        }

        public ModuleBuilder withComponent(Object object) {
            this.components.add(object);
            return this;
        }

        public ModuleBuilder withEnabledByDefault(boolean enabledByDefault) {
            this.enabledByDefault = enabledByDefault;
            return this;
        }

        public ModuleBuilder withSaveEnabled(boolean saveEnabled) {
            this.saveEnabled = saveEnabled;
            return this;
        }

        public ModuleBuilder withSaveToConfig(boolean saveToConfig) {
            this.saveToConfig = saveToConfig;
            return this;
        }

        public ModuleBuilder withLoadFromConfig(boolean loadFromConfig) {
            this.loadFromConfig = loadFromConfig;
            return this;
        }

        public ModuleBuilder withKey(int key) {
            this.key = key;
            return this;
        }

        public ModuleBuilder withAllowedKeys(int[] allowedKeys) {
            this.allowedKeys = ArrayUtils.merge(this.allowedKeys, allowedKeys);
            return this;
        }

        public ModuleBuilder withDisallowedKeys(int[] disallowedKeys) {
            this.disallowedKeys = ArrayUtils.merge(this.allowedKeys, allowedKeys);
            return this;
        }
    }

}
