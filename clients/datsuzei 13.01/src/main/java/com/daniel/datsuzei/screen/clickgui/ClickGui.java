package com.daniel.datsuzei.screen.clickgui;

import com.daniel.datsuzei.module.ModuleCategory;
import com.daniel.datsuzei.module.impl.ModuleScreenModule;
import com.daniel.datsuzei.screen.clickgui.frame.Frame;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ClickGui extends GuiScreen {

    public final ModuleScreenModule moduleScreenModule;
    private final List<Frame> frames = new ArrayList<>();

    @Override
    public void initGui() {
        // If the frames are empty, the gui wasn't initialized
        if(frames.isEmpty()) {
            // Adding frames
            final float frameWidth = 110, frameHeight = 20;
            final float frameMargin = 10;

            float frameX = 20, frameY = 20;

            for(ModuleCategory category : ModuleCategory.values()) {
                frames.add(new Frame(this, category, frameX, frameY, frameWidth, frameHeight));
                frameY += frameHeight + frameMargin;
            }
        }
        // Adding buttons
        this.buttonList.add(new GuiButton(0, this.width - 85, this.height - 25, 80, 20, "Reset"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        frames.forEach(frame -> frame.draw(mouseX, mouseY));
    }

    @Override
    public void keyTyped(char character, int key) throws IOException {
        super.keyTyped(character, key);

        frames.forEach(frame -> frame.keyTyped(character, key));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);

        frames.forEach(frame -> frame.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);

        frames.forEach(frame -> frame.mouseReleased(mouseX, mouseY, button));
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        if(guiButton.id == 0) {
            mc.displayGuiScreen(null);
            moduleScreenModule.clickGui = new ClickGui(moduleScreenModule);
        }
    }

}
