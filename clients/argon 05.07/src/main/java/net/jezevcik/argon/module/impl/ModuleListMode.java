package net.jezevcik.argon.module.impl;

import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.event.impl.RenderUiEvent;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.module.params.ModuleCategory;
import net.jezevcik.argon.module.params.ModuleParams;
import net.jezevcik.argon.renderer.UiBuilder;
import net.jezevcik.argon.system.identifier.IdentifierType;

public class ModuleListMode extends Module {

    public ModuleListMode() {
        super(ModuleParams.builder()
                .name("ModuleList")
                .category(ModuleCategory.RENDER)
                .enabledByDefault(true)
                .build());
    }

    @EventHandler
    public final void onUi(RenderUiEvent renderUiEvent) {
        final UiBuilder uiBuilder = renderUiEvent.uiBuilder;

        float moduleY = 1.5f;

        for (Module module : ParekClient.getInstance().modules) {
            if (module.isEnabled()) {
                uiBuilder.text(module.getIdentifier(IdentifierType.DISPLAY))
                        .x().absolute(uiBuilder.getWidth()).back().offset(-1.5f).finish()
                        .y().absolute(moduleY).finish()
                        .shadow().draw();

                moduleY += client.textRenderer.fontHeight + 1.5f;
            }
        }
    }

}
