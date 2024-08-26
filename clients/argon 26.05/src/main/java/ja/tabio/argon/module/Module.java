package ja.tabio.argon.module;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.Identifiable;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.interfaces.Nameable;
import ja.tabio.argon.interfaces.Toggleable;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.module.extension.Extension;
import ja.tabio.argon.setting.Setting;
import ja.tabio.argon.setting.group.SettingGroup;
import ja.tabio.argon.setting.interfaces.Settings;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class Module implements Nameable, Identifiable, Settings, Toggleable, Minecraft {

    private final List<Extension> extensions = new ArrayList<>();

    private final boolean defaultEnabled;

    private final String name, displayName;
    public final ModuleCategory moduleCategory;

    public int key;
    private boolean enabled;

    public Module(ModuleParams moduleParams) {
        this.name = moduleParams.name;
        this.displayName = moduleParams.displayName;
        this.moduleCategory = moduleParams.moduleCategory;

        this.key = moduleParams.key;

        this.defaultEnabled = moduleParams.enabled;

        register();
    }

    public void postInit() {
        lookupSettings(getClass(), this);

        if (defaultEnabled)
            setEnabled(false);
    }

    private final Map<Object, List<Setting<?>>> objectListMap = new HashMap<>();

    public void lookupSettings(Class<?> clazz, Object o) {
        final List<Setting<?>> settings = new ArrayList<>();

        for (Field field : clazz.getFields()) {
            try {
                final Object instance = field.get(o);

                // If the field is a setting group, perform reflection on it instead
                if (instance instanceof SettingGroup settingGroup) {
                    if (settingGroup instanceof Extension extension)
                        extensions.add(extension);

                    lookupSettings(settingGroup.getClass(), settingGroup);
                    settingGroup.updateSettings(objectListMap.get(settingGroup));

                    continue;
                }

                // If the field is a setting, then add its instance
                if (instance instanceof Setting<?> setting) {
                    if (getSettings().contains(setting))
                        continue;

                    addSetting(setting);
                    settings.add(setting);
                }
            } catch (Exception e) {
                throw new IllegalStateException("Failed to process field", e);
            }
        }

        objectListMap.put(o, settings);
    }

    public void enable() {
        if (enabled)
            return;

        enabled = true;

        if (onEnable()) {
            Argon.getInstance().eventBus.subscribe(this);
            extensions.forEach(Extension::onModuleEnable);
        } else {
            disable();
        }
    }

    public void disable() {
        if (!enabled)
            return;

        enabled = false;

        extensions.forEach(Extension::onModuleDisable);
        Argon.getInstance().eventBus.unsubscribe(this);

        if (!onDisable())
            enable();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled)
            enable();
        else
            disable();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    protected boolean onEnable() { return true; }

    protected boolean onDisable() { return true; }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getUniqueIdentifier() {
        return String.format("Module-%s-%s", moduleCategory.getUniqueIdentifier(), getName());
    }

    public static class ModuleParams {
        final String name;
        final String displayName;
        final ModuleCategory moduleCategory;
        final int key;
        final boolean enabled;

        public ModuleParams(String name, String displayName, ModuleCategory moduleCategory, int key, boolean enabled) {
            this.name = name;
            this.displayName = displayName;
            this.moduleCategory = moduleCategory;
            this.key = key;
            this.enabled = enabled;
        }

        public static ModuleParamsBuilder builder() {
            return new ModuleParamsBuilder();
        }

        public static class ModuleParamsBuilder {
            String name;
            String displayName;
            ModuleCategory category;
            int key = GLFW.GLFW_KEY_UNKNOWN;
            boolean enabled = false;

            public ModuleParamsBuilder name(String name) {
                this.name = name;
                return this;
            }

            public ModuleParamsBuilder displayName(String displayName) {
                this.displayName = displayName;
                return this;
            }

            public ModuleParamsBuilder category(ModuleCategory category) {
                this.category = category;
                return this;
            }

            public ModuleParamsBuilder key(int key) {
                this.key = key;
                return this;
            }

            public ModuleParamsBuilder enabled(boolean enabled) {
                this.enabled = enabled;
                return this;
            }

            public ModuleParams build() {
                if (name == null || category == null)
                    throw new NullPointerException("Missing required module information!");

                return new ModuleParams(name, displayName == null ? name : displayName, category, key, enabled);
            }
        }

    }

}
