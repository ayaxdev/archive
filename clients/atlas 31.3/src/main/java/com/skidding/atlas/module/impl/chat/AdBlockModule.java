package com.skidding.atlas.module.impl.chat;

import com.skidding.atlas.event.impl.network.HandlePacketEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;

import java.util.regex.Pattern;

public class AdBlockModule extends ModuleFeature {

    private final String adBlockerPattern = "(?!.+: )(/?(((party join|join party)|p join|(guild join)|(join guild)|g join) \\\\w{1,16})|/?(party me|visit me|duel me|my ah|my smp)|(twitch.tv)|(youtube.com|youtu.be)|(/(visit|ah) \\\\w{1,16}|(visit /\\\\w{1,16})|(/gift)|(gilde)|(lowballing|lowbaling|lowvaling|lowvaluing|lowballer)))";
    private final Pattern adBlockerRegex = Pattern.compile(adBlockerPattern, Pattern.CASE_INSENSITIVE);

    private final String beggingPattern = "(?!.+: )([^\\[](vip|mvp|mpv|vpi)|(please|pls|plz|rank ?up|rank ?upgrade)|(buy|upgrade|gift|give) (rank|me)|(gifting|gifters)|( beg |begging|beggers))";
    private final Pattern beggingRegex = Pattern.compile(beggingPattern, Pattern.CASE_INSENSITIVE);

    public AdBlockModule() {
        super(new ModuleBuilder("AdBlock", "Blocks common chat advertisements", ModuleCategory.CHAT));
    }

    @EventHandler
    public final void onHandlePacket(HandlePacketEvent handlePacketEvent) {
        if(handlePacketEvent.packet instanceof S02PacketChat s02PacketChat) {
            final String message = EnumChatFormatting.getTextWithoutFormattingCodes(s02PacketChat.getChatComponent().getUnformattedText().toLowerCase());

            if((message.startsWith("-") && message.endsWith("-")) || (message.startsWith("▬") && message.endsWith("▬")) || (message.startsWith("≡") && message.endsWith("≡")) || (!message.contains(": ")) || (message.contains(mc.getSession().getUsername().toLowerCase())))
                return;

            if(adBlockerRegex.matcher(message).find(0))
                handlePacketEvent.cancelled = true;

            if(beggingRegex.matcher(message).find(0))
                handlePacketEvent.cancelled = true;
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

}
