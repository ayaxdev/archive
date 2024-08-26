package com.skidding.atlas.module.impl.chat;

import com.skidding.atlas.event.impl.network.HandlePacketEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;

import java.util.regex.Pattern;

public class ConnectMessageBlocker extends ModuleFeature {

    private final String connectedMessagePattern =  "^(You are currently connected to server \\S+)|(Sending you to \\S+!)|(Sending you to \\S+)|(Sending to server \\S+)|(SERVER FOUND! Sending to \\S+!)|(Warping you to your SkyBlock island\\.{3})|(Warping\\.{3})|(Sending a visit request\\.{3})|(Finding player\\.{3})|(Request join for (?:Hub|Dungeon Hub) (?:.{2,4} \\S+|\\S+))|(Found an in-progress .+ game! Teleporting you to \\S+)|(Returning you to the lobby!)$";
    private final Pattern connectedMessageRegex = Pattern.compile(connectedMessagePattern);

    public ConnectMessageBlocker() {
        super(new ModuleBuilder("ConnectMessageBlocker", "Blocks messages such as 'connected to...', 'server found: joining...', ...", ModuleCategory.CHAT));
    }

    @EventHandler
    public final void onHandlePacket(HandlePacketEvent handlePacketEvent) {
        if (handlePacketEvent.packet instanceof S02PacketChat s02PacketChat) {
            final String message = EnumChatFormatting.getTextWithoutFormattingCodes(s02PacketChat.getChatComponent().getUnformattedText());

            if(connectedMessageRegex.matcher(message).matches()) {
                handlePacketEvent.cancelled = true;
            }
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

}
