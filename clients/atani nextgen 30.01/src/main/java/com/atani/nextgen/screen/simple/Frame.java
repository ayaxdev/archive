package com.atani.nextgen.screen.simple;

import com.atani.nextgen.font.FontManager;
import com.atani.nextgen.module.ModuleCategory;
import com.atani.nextgen.module.ModuleFeature;
import com.atani.nextgen.module.ModuleManager;
import com.atani.nextgen.util.minecraft.MinecraftClient;
import com.atani.nextgen.util.render.DrawUtil;
import de.florianmichael.rclasses.math.Arithmetics;
import de.florianmichael.rclasses.math.integration.Boundings;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Frame implements MinecraftClient {

    private final GuiScreen parent;
    public final ModuleCategory moduleCategory;
    public float posX, posY;
    public final float width, height;

    public Frame(GuiScreen parent, ModuleCategory moduleCategory, float posX, float posY, float width, float height) {
        this.parent = parent;
        this.moduleCategory = moduleCategory;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    final FontRenderer roboto19 = FontManager.getSingleton().get("Roboto", 19);

    public void render(int mouseX, int mouseY) {
        DrawUtil.drawRectRelative(posX, posY, width, height, new Color(0, 0, 0, 180).getRGB());
        roboto19.drawXYCenteredStringWithShadow(moduleCategory.name, posX + width / 2, posY + height / 2, -1);

        final List<ModuleFeature> modules = new ArrayList<>(ModuleManager.getSingleton().getByCategory(moduleCategory));
        modules.sort((o1, o2) -> roboto19.getStringWidth(o1.getName()) - roboto19.getStringWidth(o2.getName()));

        float moduleY = posY + height;

        for(ModuleFeature moduleFeature : modules) {
            DrawUtil.drawRectRelative(posX, moduleY, width, height, new Color(0, 0, 0, 180).getRGB());
            roboto19.drawXYCenteredStringWithShadow(moduleFeature.name, posX + width / 2, moduleY + height / 2, -1);

            moduleY += height;
        }
    }

    public boolean mouseClick(int mouseX, int mouseY, int button) {
        final List<ModuleFeature> modules = new ArrayList<>(ModuleManager.getSingleton().getByCategory(moduleCategory));
        modules.sort((o1, o2) -> roboto19.getStringWidth(o1.getName()) - roboto19.getStringWidth(o2.getName()));


        float moduleY = posY + height;

        for(ModuleFeature moduleFeature : modules) {
            if(Boundings.isInBounds(mouseX, mouseY, posX, moduleY, width, height)) {
                if(button == 1)
                    mc.displayGuiScreen(new Window(parent, moduleFeature));
                else if(button == 0)
                    moduleFeature.toggleEnabled();

                return true;
            }

            moduleY += height;
        }

        return false;
    }

}
