package ja.tabio.argon.module.impl.render;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.impl.ToastEvent;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.RegisterModule;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.setting.impl.BooleanSetting;
import net.minecraft.client.toast.SystemToast;

@RegisterModule
public class ToastManager extends Module {

    public final BooleanSetting hideChatVerificationAlerts = new BooleanSetting("HideChatVerificationAlerts", true);

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
