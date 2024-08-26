package net.jezevcik.argon.module.impl;

import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.config.setting.impl.BooleanSetting;
import net.jezevcik.argon.event.impl.ToastEvent;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.module.params.ModuleCategory;
import net.jezevcik.argon.module.params.ModuleParams;
import net.minecraft.client.toast.SystemToast;

public class ToastManager extends Module {

    public final BooleanSetting hideChatVerificationAlerts = new BooleanSetting("HideChatVerificationAlerts", true, this.config);

    public ToastManager() {
        super(ModuleParams.builder()
                .name("ToastManager")
                .category(ModuleCategory.RENDER)
                .build());
    }

    @EventHandler
    public final void onToast(ToastEvent toastEvent) {
        if (hideChatVerificationAlerts.getValue() && toastEvent.toast.getType() == SystemToast.Type.UNSECURE_SERVER_WARNING)
            toastEvent.cancelled = true;
    }

}
