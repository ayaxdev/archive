package com.skidding.atlas.screen.main;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.font.FontManager;
import com.skidding.atlas.screen.alts.AltManagerUI;
import com.skidding.atlas.screen.elements.ButtonElement;
import com.skidding.atlas.util.animation.Animation;
import com.skidding.atlas.util.animation.Direction;
import com.skidding.atlas.util.animation.impl.DecelerateAnimation;
import com.skidding.atlas.util.render.color.ColorUtil;
import net.minecraft.client.gui.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMenuUI extends GuiScreen {
    public Animation xPos = new DecelerateAnimation(1000, 0, Direction.FORWARDS);
    public Animation yPos = new DecelerateAnimation(1000, 0, Direction.FORWARDS);
    public List<ButtonElement> guiButtons = new ArrayList<>();

    public void initGui() {
        this.xPos.reset();
        this.yPos.reset();

        this.guiButtons.clear();
        this.guiButtons.add(new ButtonElement(0, 0, 25, "Singleplayer"));
        this.guiButtons.add(new ButtonElement(1, 0, 47, "Multiplayer"));
        this.guiButtons.add(new ButtonElement(2, 0, 69, "Options"));
        this.guiButtons.add(new ButtonElement(3, 0, 91, "Alt Manager"));
        this.guiButtons.add(new ButtonElement(4, 0, 113, "Exit"));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(this.mc);
        xPos.endPoint = sr.getScaledWidth_double();
        yPos.endPoint = sr.getScaledHeight_double() / 4 * 3;
        Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(25, 25, 25).getRGB());
        FontManager.getSingleton().get("Roboto", 36).drawCenteredString(AtlasClient.NAME, (float)(this.xPos.getOutput() / 2.0), (float)(this.yPos.getOutput() / 2.0 - 20.0), ColorUtil.getRainbow(4.0f, 0.5f, 1.0f).getRGB());
        for (ButtonElement btn : this.guiButtons) {
            btn.draw(mouseX, mouseY, (int)(this.xPos.getOutput() / 2.0 - 100.0), (int)(this.yPos.getOutput() / 2.0));
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (ButtonElement btn : this.guiButtons) {
            if (btn.click(mouseX, mouseY, mouseButton)) {
                switch (btn.displayString) {
                    case "Singleplayer" -> mc.displayGuiScreen(new GuiSelectWorld(new MainMenuUI()));
                    case "Multiplayer" -> mc.displayGuiScreen(new GuiMultiplayer(new MainMenuUI()));
                    case "Options" -> mc.displayGuiScreen(new GuiOptions(new MainMenuUI(), mc.gameSettings));
                    case "Alt Manager" -> mc.displayGuiScreen(new AltManagerUI());
                    case "Exit" -> mc.shutdown();
                }
            }
        }
    }
}
