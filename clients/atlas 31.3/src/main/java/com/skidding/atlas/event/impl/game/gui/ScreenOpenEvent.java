package com.skidding.atlas.event.impl.game.gui;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.GuiScreen;

@AllArgsConstructor
public class ScreenOpenEvent extends Event {
    public GuiScreen gui;
}
