package com.skidding.atlas.util.minecraft.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class ChatUtil {

    public static void print(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(STR."\{EnumChatFormatting.BOLD}(Atlas Client) :\{EnumChatFormatting.RESET} \{message}"));
    }

}
