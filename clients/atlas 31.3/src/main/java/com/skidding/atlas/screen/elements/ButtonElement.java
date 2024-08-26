package com.skidding.atlas.screen.elements;

import java.awt.Color;

import com.skidding.atlas.font.FontManager;
import com.skidding.atlas.util.minecraft.IMinecraft;
import com.skidding.atlas.util.render.shader.usage.util.DrawRoundUtil;
import de.florianmichael.rclasses.math.integration.Boundings;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Mouse;

public class ButtonElement extends GuiButton implements IMinecraft {
    public int initX;
    public int initY;

    public ButtonElement(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
        this.initX = x;
        this.initY = y;
    }

    public void draw(int mouseX, int mouseY, int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
        DrawRoundUtil.INSTANCE.drawRound(x + this.initX, y + this.initY, this.width, this.height, 3, this.hovered(mouseX, mouseY) ? new Color(30, 30, 30, 100) : new Color(20, 20, 20, 100));
        FontManager.getSingleton().get("Comfortaa", 18).drawCenteredString(this.displayString, (float)(x + this.initX + this.width / 2), y + this.initY + (float) this.height / 2 - FontManager.getSingleton().get("Comfortaa", 18).getHeight() / 2, Color.WHITE.getRGB());
    }

    public boolean click(int mouseX, int mouseY, int mouseButton) {
        return mouseButton == 0 && Mouse.isButtonDown(mouseButton) && this.hovered(mouseX, mouseY);
    }

    public boolean hovered(int mouseX, int mouseY) {
        return Boundings.isInBounds(mouseX, mouseY, this.xPosition + this.initX, this.yPosition + this.initY, this.width, this.height);
    }
}
