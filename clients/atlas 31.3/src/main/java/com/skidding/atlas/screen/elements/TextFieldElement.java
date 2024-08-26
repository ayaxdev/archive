package com.skidding.atlas.screen.elements;

import com.skidding.atlas.font.FontManager;
import com.skidding.atlas.util.minecraft.IMinecraft;
import com.skidding.atlas.util.render.shader.usage.util.DrawRoundUtil;
import de.florianmichael.rclasses.math.integration.Boundings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class TextFieldElement extends GuiButton implements IMinecraft {
    public int initX;
    public int initY;

    public boolean listening;
    public String placeholderText;

    public TextFieldElement(int buttonId, int x, int y, String fieldText, String placeholderText) {
        super(buttonId, x, y, fieldText);
        this.initX = x;
        this.initY = y;
        this.placeholderText = placeholderText;
    }

    public void draw(int mouseX, int mouseY, int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
        DrawRoundUtil.INSTANCE.drawRound(x + this.initX, y + this.initY, this.width, this.height, 3, this.hovered(mouseX, mouseY) ? new Color(30, 30, 30, 100) : new Color(20, 20, 20, 100));

        final String selectedText = this.displayString + (this.listening && System.currentTimeMillis() % 1000 < 500 ? "_" : "");
        final String placeholderText = this.placeholderText + (this.listening && System.currentTimeMillis() % 1000 < 500 ? "_" : "");
        FontManager.getSingleton().get("Comfortaa", 18).drawCenteredString(this.displayString.isEmpty() ? placeholderText : selectedText, (float)(x + this.initX + this.width / 2), y + this.initY + (float) this.height / 2 - FontManager.getSingleton().get("Comfortaa", 18).getHeight() / 2, this.hovered(mouseX, mouseY) ? Color.WHITE.getRGB() : Color.LIGHT_GRAY.getRGB());
    }

    public void click(int mouseX, int mouseY, int mouseButton) {
        listening = mouseButton == 0 & Mouse.isButtonDown(0) && this.hovered(mouseX, mouseY);
    }

    public boolean type(char typedChar, int keyCode) {
        if (!listening) return false;

        if(GuiScreen.isKeyComboCtrlV(keyCode)) {
            displayString += GuiScreen.getClipboardString();
            return true;
        }

        if (keyCode == 1 || keyCode == 28) {
            listening = false;
            return true;
        }

        if (Character.isISOControl(typedChar) && keyCode != Keyboard.KEY_BACK) {
            return true;
        }

        displayString = (keyCode == Keyboard.KEY_BACK && !displayString.isEmpty()) ?
                displayString.substring(0, displayString.length() - 1) :
                displayString + ((ChatAllowedCharacters.isAllowedCharacter(typedChar)) ? typedChar : "");

        return false;
    }

    public boolean hovered(int mouseX, int mouseY) {
        return Boundings.isInBounds(mouseX, mouseY, this.xPosition + this.initX, this.yPosition + this.initY, this.width, this.height);
    }
}
