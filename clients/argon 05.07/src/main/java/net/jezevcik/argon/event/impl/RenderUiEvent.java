package net.jezevcik.argon.event.impl;

import net.jezevcik.argon.renderer.UiBuilder;
import net.minecraft.client.gui.DrawContext;

public class RenderUiEvent {

    public final UiBuilder uiBuilder;
    public final float tickDelta;

    public RenderUiEvent(DrawContext drawContext, float tickDelta) {
        this.uiBuilder = new UiBuilder(drawContext);
        this.tickDelta = tickDelta;
    }
}
