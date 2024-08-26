package net.jezevcik.argon.utils.game;

import net.jezevcik.argon.system.minecraft.Minecraft;

/**
 * A set of methods for helping with interactions with the game client
 */
public class ClientUtils implements Minecraft {

    public static boolean inGame() {
        return client.player != null && client.world != null;
    }

}
