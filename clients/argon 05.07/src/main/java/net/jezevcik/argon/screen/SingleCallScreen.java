package net.jezevcik.argon.screen;

import net.jezevcik.argon.renderer.UiBuilder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;

public abstract class SingleCallScreen extends Screen implements ScreenCalls {

    protected SingleCallScreen(Text title) {
        super(title);
    }

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        return run(new ScreenCall(MouseAction.PRESS, mouseX, mouseY, button));
    }

    @Override
    public final boolean mouseReleased(double mouseX, double mouseY, int button) {
        return run(new ScreenCall(MouseAction.RELEASE, mouseX, mouseY, button));
    }

    @Override
    public final void render(DrawContext context, int mouseX, int mouseY, float delta) {
        run(new ScreenCall(new UiBuilder(context), delta, mouseX, mouseY));
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return run(new ScreenCall(chr, modifiers));
    }

    @Override
    public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return run(new ScreenCall(keyCode, scanCode, modifiers));
    }

}
