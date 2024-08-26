package ja.tabio.argon.utils.chat;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.Minecraft;
import net.minecraft.text.Text;

public class ChatUtils implements Minecraft {

    public static void sendMessage(String message) {
        if (mc.player == null) return;

        mc.player.sendMessage(Text.of(String.format(
                "%s >>> %s", Argon.CYRILLIC_NAME, message
        )));
    }

}
