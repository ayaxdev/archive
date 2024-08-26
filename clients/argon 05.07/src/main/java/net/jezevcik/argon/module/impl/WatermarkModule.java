package net.jezevcik.argon.module.impl;

import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.event.impl.RenderUiEvent;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.module.params.ModuleCategory;
import net.jezevcik.argon.module.params.ModuleParams;
import net.jezevcik.argon.renderer.UiBuilder;
import net.jezevcik.argon.utils.game.TextUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;

public class WatermarkModule extends Module {

    public WatermarkModule() {
        super(ModuleParams.builder()
                .name("Watermark")
                .category(ModuleCategory.RENDER)
                .enabledByDefault(true)
                .build());
    }

    private static final Text watermark = ((MutableText) Text.of(ParekClient.DISPLAY_NAME)).append(" ").append(ParekClient.VERSION).setStyle(Style.EMPTY.withBold(true));
    private static final Text subtext = TextUtils.gradient(ParekClient.SUBTEXT, Style.EMPTY.withBold(true), new Color(240, 0, 0), new Color(180, 0, 0));

    @EventHandler
    public final void onUi(RenderUiEvent renderUiEvent) {
        final UiBuilder uiBuilder = renderUiEvent.uiBuilder;

        uiBuilder.text(watermark, 1.5f, 1.5f, -1, true);
        uiBuilder.text(subtext, 1.5f, 10.5f, -1, true);
    }

}
