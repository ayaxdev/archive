package ja.tabio.argon.utils.player;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.IMinecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ConcurrentModificationException;

public class ChatUtil implements IMinecraft {

    public static final String PREFIX = EnumChatFormatting.BOLD + "(Argon) " + EnumChatFormatting.RESET;
    public static final String PREFIX_IMPORTANT = EnumChatFormatting.BOLD + "(Argon) " + EnumChatFormatting.DARK_RED + "(!) " + EnumChatFormatting.RESET;

    public static void send(String message) {
        try {
            mc.thePlayer.addChatMessage(new ChatComponentText(PREFIX + message));
        } catch (ConcurrentModificationException e) {
            Argon.getInstance().logger.error("Failed to send chat message", e);
        }
    }

    public static void sendImportant(String message) {
        try {
            mc.thePlayer.addChatMessage(new ChatComponentText(PREFIX_IMPORTANT + message));
        } catch (ConcurrentModificationException e) {
            Argon.getInstance().logger.error("Failed to send chat message", e);
        }
    }

}
