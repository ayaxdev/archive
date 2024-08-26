package net.jezevcik.argon.system.minecraft;

import net.minecraft.client.MinecraftClient;

public interface Minecraft {

    MinecraftClient client = MinecraftClient.getInstance();

    static boolean inGame() {
        return client.player != null && client.world != null;
    }

}
