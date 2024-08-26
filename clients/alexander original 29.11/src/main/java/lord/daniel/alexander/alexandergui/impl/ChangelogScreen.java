package lord.daniel.alexander.alexandergui.impl;

import lord.daniel.alexander.Modification;
import lord.daniel.alexander.alexandergui.AlexanderGuiScreen;
import net.minecraft.util.EnumChatFormatting;

/**
 * Written by Daniel. on 05/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class ChangelogScreen extends AlexanderGuiScreen {

    @Override
    public String getName() {
        return "Changelog";
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        float x = 100 + 3, y = 90 + 3;

        mc.fontRendererObj.drawStringWithShadow(Modification.NAME + " b" + Modification.VERSION, x, y, -1);

        y += mc.fontRendererObj.FONT_HEIGHT + 1;

        for(String s : Modification.CHANGELOG) {
            mc.fontRendererObj.drawStringWithShadow(s.replace("+", EnumChatFormatting.GREEN + "+")
                    .replace("-", EnumChatFormatting.RED + "-")
                    .replace("/", EnumChatFormatting.YELLOW + "/"), x, y, -1);

            y += mc.fontRendererObj.FONT_HEIGHT + 1;
        }
    }

}
