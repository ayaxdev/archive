package ja.tabio.argon.processor.impl;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.impl.DisplayScreenEvent;
import ja.tabio.argon.processor.Processor;
import ja.tabio.argon.processor.annotation.ProcessorData;
import ja.tabio.argon.screen.main.CMainMenu;
import net.minecraft.client.gui.GuiMainMenu;

@ProcessorData(name = "Screens")
public class ScreenOverrideProcessor extends Processor {

    @EventHandler
    public final void onScreen(final DisplayScreenEvent displayScreenEvent) {
        if (displayScreenEvent.guiScreen instanceof GuiMainMenu)
            displayScreenEvent.guiScreen = new CMainMenu();
    }

}
