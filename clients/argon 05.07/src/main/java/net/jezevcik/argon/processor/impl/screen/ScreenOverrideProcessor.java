package net.jezevcik.argon.processor.impl.screen;

import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.event.impl.OpenScreenEvent;
import net.jezevcik.argon.processor.Processor;
import net.jezevcik.argon.screen.mainmenu.MainMenuScreen;
import net.jezevcik.argon.system.minecraft.Minecraft;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;

public class ScreenOverrideProcessor extends Processor {

    public ScreenOverrideProcessor() {
        super("ScreenOverride");
    }

    @EventHandler
    public final void onOpen(OpenScreenEvent openScreenEvent) {
        // Multiplayer warning
        if (openScreenEvent.screen instanceof MultiplayerWarningScreen)
            openScreenEvent.screen = new MultiplayerScreen(null);

        // Main menu
        if (openScreenEvent.screen instanceof TitleScreen ||
                (!Minecraft.inGame() && openScreenEvent.screen == null))
            openScreenEvent.screen = new MainMenuScreen();
    }

}
