package com.skidding.atlas.processor.impl;

import com.skidding.atlas.event.impl.game.gui.ScreenOpenEvent;
import com.skidding.atlas.processor.Processor;
import com.skidding.atlas.screen.main.MainMenuUI;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.client.gui.GuiMainMenu;

public final class GUIOverrideProcessor extends Processor {

    @EventHandler(priority = -9999)
    public void onOpenGui(ScreenOpenEvent screenOpenEvent) {
        if (screenOpenEvent.gui instanceof GuiMainMenu) {
            screenOpenEvent.gui = new MainMenuUI();
        }
        if (screenOpenEvent.gui == null && mc.theWorld == null) {
            screenOpenEvent.gui = new MainMenuUI();
        }
    }

}
