package com.skidding.atlas.util.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;

public interface IMinecraft {

    Minecraft mc = Minecraft.getMinecraft();

    default EntityPlayerSP getPlayer() {
        return mc.thePlayer;
    }

    default WorldClient getWorld() {
        return mc.theWorld;
    }

    default GameSettings getGameSettings() {
        return mc.gameSettings;
    }

}
