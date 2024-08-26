package com.skidding.atlas.screen.simple;

import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.util.animation.Direction;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

public class SimpleFrameUI extends GuiScreen {

    public long scroll;
    public final ArrayList<Frame> frames = new ArrayList<>();

    public void initGui() {
        final float margin = 10;
        final float frameWidth = 100 + margin;
        float width = 0;

        for(ModuleCategory _ : ModuleCategory.values()) {
            width += frameWidth;
        }

        final float frameOffset = this.width / 2f - width / 2;
        float frameX = frameOffset;

        if(frames.isEmpty()) {
            for(ModuleCategory category : ModuleCategory.values()) {
                frames.add(new Frame(category, frameX, Math.min(frameOffset, 50), frameWidth - margin, 16));

                frameX += frameWidth;
            }
        }

        float lastFramePosX = frameOffset - frameWidth;


        for(Frame frame : frames) {
            frame.init(lastFramePosX);

            lastFramePosX = frame.posX;
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        scroll += (long) (Mouse.getDWheel() / 10F);

        boolean closing = true;

        for(Frame frame : frames) {
            if(!frame.closing || !frame.openingAnimation.finished(Direction.BACKWARDS)) {
                closing = false;
            }
        }

        if(closing) {
            mc.displayGuiScreen(null);

            for(Frame frame : frames) {
                frame.end();
            }

            return;
        }

        for(Frame frame : frames) {
            frame.scrollAnimation.interpolate(scroll);

            frame.render(mouseX, mouseY);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for(Frame frame : frames) {
            if(frame.keyTyped(typedChar, keyCode))
                return;
        }

        if(keyCode == Keyboard.KEY_ESCAPE)
            return;

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for(Frame frame : frames) {
            if(frame.mouseClick(mouseX, mouseY, mouseButton))
                break;
        }
    }
}
