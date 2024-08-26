package ja.tabio.argon.interfaces;

import net.minecraft.client.MinecraftClient;

public interface Minecraft {

    MinecraftClient mc = MinecraftClient.getInstance();

    static boolean inGame() {
        return mc.player != null && mc.world != null;
    }

}
