package ja.tabio.argon.module.impl.render;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.impl.LoadingOverlayEvent;
import ja.tabio.argon.event.impl.ReloadingLoadingOverlayEvent;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.RegisterModule;
import ja.tabio.argon.module.enums.ModuleCategory;

@RegisterModule
public class FastLoadingOverlay extends Module {

    public FastLoadingOverlay() {
        super(ModuleParams.builder()
                .name("FastLoadingOverlay")
                .category(ModuleCategory.RENDER)
                .build());
    }

    @EventHandler
    public final void onLoadingOverlay(LoadingOverlayEvent loadingOverlayEvent) {
        if (loadingOverlayEvent.reloadCompleteTime != -1) {
            mc.setOverlay(null);
        }
    }

    @EventHandler
    public final void onReload(ReloadingLoadingOverlayEvent reloadingLoadingOverlayEvent) {
        reloadingLoadingOverlayEvent.reloading = false;
    }

}
