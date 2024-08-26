package lord.daniel.alexander.alexandergui.impl;

import lord.daniel.alexander.alexandergui.AlexanderGuiScreen;
import lord.daniel.alexander.util.render.RenderUtil;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

/**
 * Written by Daniel. on 05/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class BackgroundScreen extends AlexanderGuiScreen {

    @Override
    public String getName() {
        return "Background";
    }

    private static float scroll;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        scroll = Math.min(scroll + Mouse.getDWheel() / 10f, 0);

        float x = 100;
        float width = this.width - x;

        float y = 92 + scroll;

        RenderUtil.startScissorBox();
        RenderUtil.drawScissorBox(100, 90.5f, this.width - 100, this.height - 90.5f);

        for(String s : mainMenuModule.shaderMode.getModes()) {
            RenderUtil.drawRect(x + width / 2f - 150, y, 300, 15, new Color(0, 0, 0, 120));
            mc.fontRendererObj.drawStringWithShadow(s, x + width / 2f - 150 + 300 / 2f - mc.fontRendererObj.getStringWidth(s) / 2f, y + 15 / 2f - mc.fontRendererObj.FONT_HEIGHT / 2f, RenderUtil.isHovered(mouseX, mouseY, x + width / 2f - 150, y, 300, 15) ? Color.LIGHT_GRAY.getRGB() : -1);

            y += 17;
        }

        RenderUtil.endScissorBox();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        float x = 100;
        float width = this.width - x;

        float y = 92 + scroll;

        if(RenderUtil.isHovered(mouseX, mouseY, 100, 90, this.width - 100, this.height - 90)) {
            for(String s : mainMenuModule.shaderMode.getModes()) {
                if(RenderUtil.isHovered(mouseX, mouseY, x + width / 2f - 150, y, 300, 15)) {
                    mainMenuModule.shaderMode.setValue(s);
                }

                y += 17;
            }
        }
    }

}
