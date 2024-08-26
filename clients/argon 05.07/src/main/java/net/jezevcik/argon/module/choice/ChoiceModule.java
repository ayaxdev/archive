package net.jezevcik.argon.module.choice;

import net.jezevcik.argon.config.setting.impl.ModeSetting;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.module.extension.Extension;
import net.jezevcik.argon.module.params.ModuleParams;
import net.jezevcik.argon.utils.objects.ObjectUtils;

public abstract class ChoiceModule extends Module {

    private Choice lastMode;

    public final ModeSetting mode;

    public ChoiceModule(String choiceType, ModuleParams moduleParams) {
        super(moduleParams);

        mode = new ModeSetting(choiceType, getChoiceNames()[0], getChoiceNames(), this.config)
                .change((newValue, oldValue) -> {
                    if (!isEnabled())
                        return;

                    if (lastMode != null) {
                        lastMode.disable();
                    }

                    lastMode = ObjectUtils.getByString(getChoiceObjects(), newValue);
                    lastMode.enable();
                });

        for (int i = 0; i < getChoiceObjects().length; i++) {
            final Extension mode = getChoiceObjects()[i];
            final String modeName = getChoiceNames()[i];

            mode.config.hidden = true;
            mode.config.visibility = () -> this.mode.getValue().equalsIgnoreCase(modeName);
        }
    }

    @Override
    public final void onEnable() {
        lastMode = ObjectUtils.getByString(getChoiceObjects(), mode.getValue());
        lastMode.enable();
    }

    @Override
    public final void onDisable() {
        if (lastMode != null)
            lastMode.disable();
    }


    public abstract String[] getChoiceNames();

    public abstract Choice[] getChoiceObjects();
}
