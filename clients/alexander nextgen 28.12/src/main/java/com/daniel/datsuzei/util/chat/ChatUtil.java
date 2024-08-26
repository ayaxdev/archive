package com.daniel.datsuzei.util.chat;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.util.interfaces.MinecraftClient;
import lombok.experimental.UtilityClass;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

@UtilityClass
public class ChatUtil implements MinecraftClient {

    public void display(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText(DatsuzeiClient.CHAT_PREFIX + message));
    }

}
