package ja.tabio.argon.event.impl;

import net.minecraft.client.gui.DrawContext;

public class Render2DEvent {
    public DrawContext drawContext;
    public int screenWidth, screenHeight;
    public float tickDelta;

    public Render2DEvent(DrawContext drawContext, int screenWidth, int screenHeight, float tickDelta) {
        this.drawContext = drawContext;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.tickDelta = tickDelta;
    }

}