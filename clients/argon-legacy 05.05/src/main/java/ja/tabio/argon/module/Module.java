package ja.tabio.argon.module;

import com.alibaba.fastjson2.JSONObject;
import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.IMinecraft;
import ja.tabio.argon.interfaces.INameable;
import ja.tabio.argon.interfaces.ISerializable;
import ja.tabio.argon.interfaces.IToggleable;
import ja.tabio.argon.module.annotation.HackData;
import ja.tabio.argon.module.annotation.ModuleData;
import ja.tabio.argon.module.annotation.VisualData;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.setting.Setting;
import ja.tabio.argon.setting.interfaces.ISettings;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public abstract class Module implements INameable, ISettings, IToggleable, IMinecraft, ISerializable, Argon.IArgonAccess {

    public final String name, displayName;
    public final boolean alwaysRegistered;

    public final ModuleData moduleData;
    public final HackData hackData;
    public final VisualData visualData;

    public int key;
    private boolean enabled;

    protected final List<Object> extensions = new LinkedList<>();

    public Module() {
        final ModuleData moduleAnnotation = getClass().getAnnotation(ModuleData.class);
        final HackData hackAnnotation = getClass().getAnnotation(HackData.class);
        final VisualData visualAnnotation = getClass().getAnnotation(VisualData.class);

        if (moduleAnnotation == null)
            throw new IllegalStateException(String.format("Module %s is not annotated with @ModuleData", getClass().getSimpleName()));

        if (hackAnnotation == null && moduleAnnotation.category() == ModuleCategory.HACK)
            throw new IllegalStateException(String.format("Hack module %s is not annotated with @HackData", getClass().getSimpleName()));

        if (visualAnnotation == null && moduleAnnotation.category() == ModuleCategory.VISUAL)
            throw new IllegalStateException(String.format("Visual module %s is not annotated with @VisualData", getClass().getSimpleName()));

        this.moduleData = moduleAnnotation;
        this.hackData = hackAnnotation;
        this.visualData = visualAnnotation;

        this.name = moduleAnnotation.name();
        this.displayName = moduleAnnotation.displayName().equals("name") ?
                this.name : moduleAnnotation.displayName();
        this.alwaysRegistered = moduleAnnotation.alwaysRegistered();

        this.key = moduleAnnotation.key();


        if (alwaysRegistered)
            subscribe();

        setEnabled(moduleAnnotation.enabled());

        register();
    }

    public void postInit() {
        for (Field field : getClass().getFields()) {
            try {
                final Object object = field.get(this);

                if (object instanceof Setting<?> setting) {
                    addSetting(setting);
                }
            } catch (Exception e) {
                throw new IllegalStateException("Failed to process field", e);
            }
        }
    }

    public void enable() {
        if (enabled)
            return;

        enabled = true;

        onEnable();

        if (!alwaysRegistered)
            subscribe();
    }

    public void disable() {
        if (!enabled)
            return;

        enabled = false;

        if (!alwaysRegistered)
            unsubscribe();
        onDisable();
    }

    private void subscribe() {
        getBus().subscribe(this);
        for (Object o : extensions)
            getBus().subscribe(o);
    }

    private void unsubscribe() {
        for (Object o : extensions)
            getBus().unsubscribe(o);
        getBus().unsubscribe(this);
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

    protected void onEnable() { }

    protected void onDisable() { }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public JSONObject serialize() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("enabled", enabled);
        jsonObject.put("key", key);
        return jsonObject;
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        setEnabled(jsonObject.getBoolean("enabled"));
        key = jsonObject.getInteger("key");
    }

    @Override
    public String getSettingIdentifier() {
        return String.format("module.%s.%s", moduleData.category().getName(), moduleData.name());
    }
}
