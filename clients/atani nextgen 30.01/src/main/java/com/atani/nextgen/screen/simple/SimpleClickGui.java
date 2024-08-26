package com.atani.nextgen.screen.simple;

import com.atani.nextgen.module.ModuleCategory;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;

public class SimpleClickGui extends GuiScreen {

    public final ArrayList<Frame> frames = new ArrayList<>();

    public void initGui() {
        frames.clear();

        final float margin = 10;
        final float frameWidth = 110 + margin;
        float width = 0;

        for(ModuleCategory category : ModuleCategory.values()) {
            width += frameWidth;
        }

        final float frameOffset = this.width / 2f - width / 2;
        float frameX = frameOffset;

        for(ModuleCategory category : ModuleCategory.values()) {
            frames.add(new Frame(this, category, frameX, Math.min(frameOffset, 50), frameWidth - margin, 18));

            frameX += frameWidth;
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        for(Frame frame : frames) {
            frame.render(mouseX, mouseY);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for(Frame frame : frames) {
            frame.mouseClick(mouseX, mouseY, mouseButton);
        }
    }
}
