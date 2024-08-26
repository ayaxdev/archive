package ja.tabio.argon.module.mode;

import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.extension.Extension;
import ja.tabio.argon.setting.impl.ModeSetting;
import ja.tabio.argon.utils.jvm.ObjectUtils;

public abstract class ModeModule extends Module {

    private Extension lastMode;

    public final ModeSetting mode = new ModeSetting("Mode", getModeNames()[0], getModeNames())
            .change((pre, oldValue, newValue) -> {
                if (!isEnabled() || !pre)
                    return;

                if (lastMode != null)
                    lastMode.disable();

                lastMode = ObjectUtils.get(getModes(), newValue);
                lastMode.enable();
            });

    public ModeModule(ModuleParams moduleParams) {
        super(moduleParams);
    }

    @Override
    public void postInit() {
        addSetting(mode);

        super.postInit();
    }

    @Override
    public final boolean onEnable() {
        lastMode = ObjectUtils.get(getModes(), mode.getValue());
        lastMode.enable();

        return true;
    }

    @Override
    public final boolean onDisable() {
        if (lastMode != null)
            lastMode.disable();

        return true;
    }

    public abstract String[] getModeNames();

    public abstract Extension[] getModes();
}
