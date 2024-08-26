package com.daniel.datsuzei.module;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.feature.Feature;
import com.daniel.datsuzei.util.json.DeserializationUtil;
import com.google.gson.JsonObject;
import de.florianmichael.rclasses.common.ArrayUtils;
import lombok.Getter;
import lombok.Setter;
import org.lwjglx.input.Keyboard;

import java.util.function.Function;

@Getter
public abstract class ModuleFeature implements Feature {

    private final String name, description;
    private final ModuleCategory category;
    private final boolean allowSaveEnable;
    private final Function<Integer, Integer> keyCheckerFunction;

    @Setter
    private int key;
    @Setter
    private boolean saveToConfig, loadFromConfig;
    private boolean enabled;

    protected ModuleFeature(ModuleData moduleData, BindableData bindableData, ConfigurableData configurableData) {
        this.name = moduleData.name();
        this.description = moduleData.description();
        this.category = moduleData.moduleCategory();

        if (bindableData != null) {
            this.keyCheckerFunction = bindableData.getKeyChecker();
            this.key = bindableData.key();
        } else {
            this.keyCheckerFunction = (key) -> key;
            this.key = Keyboard.KEY_NONE;
        }

        if (configurableData != null) {
            this.enabled = configurableData.enabledByDefault();
            this.allowSaveEnable = configurableData.allowSaveEnable();
            this.saveToConfig = configurableData.saveToConfig();
            this.loadFromConfig = configurableData.loadFromConfig();
        } else {
            this.enabled = false;
            this.allowSaveEnable = true;
            this.saveToConfig = true;
            this.loadFromConfig = true;
        }
    }

    // Set the module's enabled state
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            onEnable();
            DatsuzeiClient.getSingleton().getEventBus().subscribe(this);
        } else {
            DatsuzeiClient.getSingleton().getEventBus().unsubscribe(this);
            onDisable();
        }
    }

    // Toggle the module's enabled state
    public void toggleEnabled() {
        setEnabled(!enabled);
    }

    // Method to be called when the module is enabled
    protected abstract void onEnable();

    // Method to be called when the module is disabled
    protected abstract void onDisable();

    // Serializes the module and returns a json object with the values enabled, key, saveToConfig and loadFromConfig
    @Override
    public JsonObject serializeFeature() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("enabled", enabled);
        jsonObject.addProperty("key", key);
        jsonObject.addProperty("saveToConfig", saveToConfig);
        jsonObject.addProperty("loadFromConfig", loadFromConfig);
        return jsonObject;
    }

    // Changes variables depending on the information in the json
    @Override
    public void deserializeFeature(JsonObject jsonObject) {
        try {
            setEnabled(DeserializationUtil.elementExists("enabled", jsonObject).getAsBoolean());
            setKey(DeserializationUtil.elementExists("key", jsonObject).getAsInt());
            setSaveToConfig(DeserializationUtil.elementExists("saveToConfig", jsonObject).getAsBoolean());
            setLoadFromConfig(DeserializationUtil.elementExists("loadFromConfig", jsonObject).getAsBoolean());
        } catch (DeserializationUtil.ElementNotFoundException e) {
            DatsuzeiClient.getSingleton().getLogger().error(STR."Failed to deserialize module \{ getName()}:", e);
        }
    }

    // Calls the logger with the message and module identifier
    protected final void warn(String warning) {
        DatsuzeiClient.getSingleton().getLogger().warn(STR."\{warning} (\{ getName()})");
    }

    protected final void error(String error) {
        DatsuzeiClient.getSingleton().getLogger().error(STR."\{error} (\{ getName()})");

    }

    protected final void info(String information) {
        DatsuzeiClient.getSingleton().getLogger().info(STR."\{information} (\{ getName()})");
    }

    // Record for holding module-specific data
    protected record ModuleData(String name, String description, ModuleCategory moduleCategory) {
        public ModuleData(String name, String description, ModuleCategory moduleCategory) {
            this.name = name;
            this.description = description;
            this.moduleCategory = moduleCategory;

            if (name.isBlank() || description.isBlank())
                throw new IllegalArgumentException("Name and description must not be blank!");

            if (name.contains(" "))
                throw new IllegalArgumentException("Name must not contain spaces!");
        }
    }

    // Record for holding bindable data
    protected record BindableData(int key, int[] allowed, int[] disallowed) {
        public BindableData(int key) {
            this(key, null, null);
        }

        public Function<Integer, Integer> getKeyChecker() {
            return (key) -> {
                if (allowed != null && !ArrayUtils.contains(allowed, key))
                    return 0;

                if (disallowed != null && ArrayUtils.contains(disallowed, key))
                    return 0;

                return key;
            };
        }
    }

    // Record for holding configurable data
    protected record ConfigurableData(boolean enabledByDefault, boolean allowSaveEnable, boolean saveToConfig, boolean loadFromConfig) {
        public ConfigurableData(boolean enabledByDefault, boolean allowSaveEnable, boolean saveToConfig, boolean loadFromConfig) {
            this.enabledByDefault = enabledByDefault;
            this.allowSaveEnable = allowSaveEnable;
            this.saveToConfig = saveToConfig;
            this.loadFromConfig = loadFromConfig;

            if (!allowSaveEnable && !saveToConfig) {
                DatsuzeiClient.getSingleton().getLogger().warn("Redundant arguments, both allowSaveEnable and saveToConfig are disabled");
            }
        }
    }

}
