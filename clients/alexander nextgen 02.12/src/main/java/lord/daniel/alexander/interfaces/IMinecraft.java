package lord.daniel.alexander.interfaces;

import lord.daniel.alexander.handler.player.PlayerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjglx.input.Keyboard;

/**
 * Written by Daniel. on 22/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
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
