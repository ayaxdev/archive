package lord.daniel.alexander.module.abstracts;

import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.event.Event;
import lord.daniel.alexander.event.impl.client.ModuleDisableEvent;
import lord.daniel.alexander.event.impl.client.ModuleEnableEvent;
import lord.daniel.alexander.interfaces.*;
import lord.daniel.alexander.module.annotations.CreateModule;
import lord.daniel.alexander.module.data.EnumModuleType;
import lord.daniel.alexander.module.exceptions.MissingAnnotationException;
import lord.daniel.alexander.settings.AbstractSetting;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.KeyBindValue;
import lord.daniel.alexander.settings.impl.string.StringValue;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Written by Daniel. on 21/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@Getter
@Setter
public abstract class AbstractModule implements Bindable, Toggleable, IMinecraft, IGameSettings, IPlayer, INetwork {

    private final String name;
    private final String[] availableDisplayNames;
    private final EnumModuleType category;
    private final EnumModuleType[] secondaryCategories;

    private final boolean alwaysRegistered, frozenState;
    private final String[] allowedHWIDs;

    private String displayName;
    private int key;
    private boolean enabled, loadFromConfig, saveToConfig, visibleInModuleList;

    private final ArrayList<AbstractSetting<?>> settings = new ArrayList<>();

    public AbstractModule() {
        CreateModule createModuleAnnotation = getClass().getAnnotation(CreateModule.class);

        if(createModuleAnnotation == null)
            throw new MissingAnnotationException(getClass());
        else {
            this.name = createModuleAnnotation.name();
            this.availableDisplayNames = createModuleAnnotation.displayNames();
            this.category = createModuleAnnotation.category();
            this.secondaryCategories = createModuleAnnotation.secondaryCategories();
            this.key = createModuleAnnotation.key();
            this.alwaysRegistered = createModuleAnnotation.alwaysRegistered();
            this.frozenState = createModuleAnnotation.frozenState();
            this.loadFromConfig = createModuleAnnotation.loadFromConfig();
            this.saveToConfig = createModuleAnnotation.saveToConfig();
            this.visibleInModuleList = createModuleAnnotation.visibleInModuleList();
            this.allowedHWIDs = createModuleAnnotation.allowedHWIDs();

            if(createModuleAnnotation.enabledOnStart())
                setEnabled(true);

            if(alwaysRegistered)
                Modification.getModification().getPubSub().subscribe(this);

            displayName = name;

            addSettings();
        }

    }

    private void addSettings() {
        ArrayList<String> displayNames = new ArrayList<>();
        displayNames.add(name);
        displayNames.addAll(Arrays.asList(availableDisplayNames));
        displayNames.add("Custom");

        new KeyBindValue("Bind", this, key).addValueChangeListeners((setting, oldValue, newValue) -> key = newValue);
        new StringModeValue("DisplayName", this, displayName, displayNames).addValueChangeListeners((setting, oldValue, newValue) -> {
            if(newValue.equals("Custom")) {
                displayName = getSettingByName("CustomName").getValueAsString();
            } else {
                displayName = newValue;
            }
        });
        new StringValue("CustomName", this, name).addValueChangeListeners((setting, oldValue, newValue) -> displayName = newValue).addVisibleCondition((StringModeValue) getSettingByName("DisplayName"), "Custom");
        new BooleanValue("LoadFromConfig", this, loadFromConfig).addValueChangeListeners((setting, oldValue, newValue) -> loadFromConfig = newValue);
        new BooleanValue("SaveToConfig", this, saveToConfig).addValueChangeListeners((setting, oldValue, newValue) -> saveToConfig = newValue);
    }


    public AbstractSetting<?> getSettingByName(String name) {
        return this.settings.stream().filter(setting -> setting.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if(this.frozenState)
            return;

        this.enabled = enabled;

        if(this.enabled) {
            defaultOnEnable();
        } else {
            defaultOnDisable();
        }
    }

    private void defaultOnEnable() {
        final ModuleEnableEvent preEvent = new ModuleEnableEvent(Event.Stage.PRE, this);
        Modification.getModification().getPubSub().publish(preEvent);
        if(preEvent.isCancelled())
            return;

        onEnable();

        final ModuleEnableEvent midEvent = new ModuleEnableEvent(Event.Stage.MID, this);
        Modification.getModification().getPubSub().publish(midEvent);
        if(midEvent.isCancelled())
            return;

        if(!alwaysRegistered)
            Modification.getModification().getPubSub().subscribe(this);

        Modification.getModification().getPubSub().publish(new ModuleEnableEvent(Event.Stage.POST, this));
    }

    private void defaultOnDisable() {
        final ModuleDisableEvent preEvent = new ModuleDisableEvent(Event.Stage.PRE, this);
        Modification.getModification().getPubSub().publish(preEvent);
        if(preEvent.isCancelled())
            return;

        if(!alwaysRegistered)
            Modification.getModification().getPubSub().unsubscribe(this);

        final ModuleDisableEvent midEvent = new ModuleDisableEvent(Event.Stage.MID, this);
        Modification.getModification().getPubSub().publish(midEvent);
        if(midEvent.isCancelled())
            return;

        onDisable();

        Modification.getModification().getPubSub().publish(new ModuleDisableEvent(Event.Stage.POST, this));
    }

    public String getDisplayName() {
        if(displayName.isEmpty())
            return getName();
        return displayName;
    }

    protected abstract void onEnable();

    protected abstract void onDisable();

    public abstract String getSuffix();
}
