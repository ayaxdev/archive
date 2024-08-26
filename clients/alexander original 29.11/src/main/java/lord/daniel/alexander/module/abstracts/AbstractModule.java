package lord.daniel.alexander.module.abstracts;

import com.google.gson.JsonObject;
import lombok.Getter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.event.impl.module.DisableModuleEvent;
import lord.daniel.alexander.event.impl.module.EnableModuleEvent;
import lord.daniel.alexander.interfaces.Methods;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.AbstractSetting;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.number.KeyBindValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.util.java.ArrayUtils;

import java.util.ArrayList;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
public abstract class AbstractModule implements Methods {

    private final String name, identifier;
    private final String[] aliases;
    private final EnumModuleType[] categories;
    private final String[] supportedIPs;

    private int key;
    private boolean saveToConfig, loadToConfig;

    private boolean enabled;
    private boolean alwaysRegistered;
    private boolean frozenState;

    private String displayName;

    private final ArrayList<AbstractSetting<?>> settings = new ArrayList<>();

    @Getter
    private String[] suffix;

    public AbstractModule() {
        ModuleData moduleData = this.getClass().getAnnotation(ModuleData.class);
        if(moduleData == null)
            throw new RuntimeException();
        this.name = moduleData.name();
        this.identifier = moduleData.identifier().isEmpty() ? moduleData.name() : moduleData.identifier();
        this.aliases = ArrayUtils.add(moduleData.aliases(), 0, name);
        this.categories = moduleData.categories().length == 0 ? new EnumModuleType[] {moduleData.enumModuleType()} : moduleData.categories();
        this.supportedIPs = moduleData.supportedIPs();
        this.key = moduleData.key();
        try {
            this.setEnabled(moduleData.enabled());
        } catch (Exception ignored) {}

        if(moduleData.alwaysRegistered()) {
            Modification.INSTANCE.getBus().subscribe(this);
            alwaysRegistered = true;
        }

        if(moduleData.frozenState()) {
            frozenState = true;
        }

        this.displayName = name;

        this.addSettings();
    }

    private void addSettings() {
        new KeyBindValue("BindKey", this, key).setValueChangeListeners((setting, oldValue, newValue) -> key = newValue);
        new StringModeValue("DisplayName", this, name, aliases).setValueChangeListeners((setting, oldValue, newValue) -> displayName = newValue);
        new BooleanValue("SaveToConfig", this, true).setValueChangeListeners((setting, oldValue, newValue) -> saveToConfig = newValue);
        new BooleanValue("LoadFromConfig", this, !ArrayUtils.contains(categories, EnumModuleType.HUD)).setValueChangeListeners((setting, oldValue, newValue) -> loadToConfig = newValue);
    }

    public AbstractSetting<?> getSettingByName(String name) {
        return this.settings.stream().filter(setting -> setting.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void toggle() {
        this.setEnabled(!this.isEnabled());
    }

    public void setEnabled(boolean enabled) {
        if(this.frozenState) {
            return;
        }

        if(enabled) {
            onModuleEnable();
        } else {
            onModuleDisable();
        }
    }

    private void onModuleEnable() {
        EnableModuleEvent enableModuleEvent = new EnableModuleEvent(this, EnableModuleEvent.Type.PRE).publishItself();
        if(enableModuleEvent.isCancelled())
            return;
        this.enabled = true;
        onEnable();
        if(!alwaysRegistered)
            Modification.INSTANCE.getBus().subscribe(this);
        new EnableModuleEvent(this, EnableModuleEvent.Type.POST).publishItself();
    }

    private void onModuleDisable() {
        DisableModuleEvent disableModuleEvent = new DisableModuleEvent(this, DisableModuleEvent.Type.PRE).publishItself();
        if(disableModuleEvent.isCancelled())
            return;
        this.enabled = false;
        if(!alwaysRegistered)
            Modification.INSTANCE.getBus().unsubscribe(this);
        onDisable();
        new DisableModuleEvent(this, DisableModuleEvent.Type.POST).publishItself();
    }

    public abstract void onEnable();
    public abstract void onDisable();

    protected void setSuffix(String... suffix) {
        this.suffix = suffix;
    }

    public JsonObject save() {
        JsonObject object = new JsonObject();
        object.addProperty("Enabled", isEnabled());
        if (!settings.isEmpty()) {
            JsonObject propertiesObject = new JsonObject();
            for (AbstractSetting<?> property : settings) {
                if(property.getValueAsString() == null)
                    continue;

                propertiesObject.addProperty(property.getName(), property.getValueAsString());
            }
            object.add("Values", propertiesObject);
        }
        return object;
    }

    public void load(JsonObject object) {
        try {
            if (object.has("Enabled"))
                setEnabled(object.get("Enabled").getAsBoolean());
        } catch (Exception e) {
            // Ignored
        }
        if (object.has("Values") && !settings.isEmpty()) {
            JsonObject propertiesObject = object.getAsJsonObject("Values");
            for (AbstractSetting<?> property : settings) {
                if (propertiesObject.has(property.getName())) {
                    property.setValueByString(propertiesObject.get(property.getName()).getAsString());
                }
            }
        }
    }

}
