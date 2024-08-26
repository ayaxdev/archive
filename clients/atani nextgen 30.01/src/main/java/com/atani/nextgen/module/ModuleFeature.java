package com.atani.nextgen.module;

import com.atani.nextgen.AtaniClient;
import com.atani.nextgen.feature.Feature;
import com.google.gson.JsonObject;
import de.florianmichael.rclasses.common.array.ArrayUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public abstract class ModuleFeature implements Feature {

    public final String name, description;
    public final ModuleCategory moduleCategory;

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
            AtaniClient.getInstance().eventPubSub.subscribe(this);
            this.components.forEach(object -> AtaniClient.getInstance().eventPubSub.subscribe(object));
        } else {
            this.components.forEach(object -> AtaniClient.getInstance().eventPubSub.unsubscribe(object));
            AtaniClient.getInstance().eventPubSub.unsubscribe(this);
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
                enabled = jsonObject.get("enabled").getAsBoolean();
            if (jsonObject.has("saveToConfig"))
                saveToConfig = jsonObject.get("saveToConfig").getAsBoolean();
            if (jsonObject.has("loadFromConfig"))
                loadFromConfig = jsonObject.get("loadFromConfig").getAsBoolean();
        }
    }

    @SuppressWarnings("unused")
    protected static class ModuleBuilder {
        private final String name, description;
        private final ModuleCategory moduleCategory;
        private boolean enabledByDefault;

        private boolean saveEnabled = true,
                saveToConfig = true,
                loadFromConfig = true;

        private final List<Object> components = new ArrayList<>();

        private int key = 0;
        private int[] allowedKeys = null, disallowedKeys = null;

        public ModuleBuilder(String name, String description, ModuleCategory moduleCategory) {
            if (name == null || description == null || moduleCategory == null)
                throw new IllegalArgumentException("Name, description and category must not be null");

            if(description.contains("."))
                throw new RuntimeException("GO KILL YOURSELF");

            this.name = name;
            this.description = description;
            this.moduleCategory = moduleCategory;
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
