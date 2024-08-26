package com.skidding.atlas.module.impl.chat;

import com.skidding.atlas.event.impl.network.HandlePacketEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;

import java.util.regex.Pattern;

public class NiceGuyBlockModule extends ModuleFeature {

    private final String blockerPattern = "(?!.+: )(gl|glhf|hf|gg|gf|good fight|good game|good luck|have a good game|autogl by sk1er)";
    private final Pattern blockerRegex = Pattern.compile(blockerPattern, Pattern.CASE_INSENSITIVE);

    public NiceGuyBlockModule() {
        super(new ModuleBuilder("NiceGuyBlock", "Blocks annoyances such as 'gl', 'gg', 'hf', ...", ModuleCategory.CHAT));
    }

    @EventHandler
    public final void onHandlePacket(HandlePacketEvent handlePacketEvent) {
        if (handlePacketEvent.packet instanceof S02PacketChat s02PacketChat) {
            final String message = EnumChatFormatting.getTextWithoutFormattingCodes(s02PacketChat.getChatComponent().getUnformattedText().toLowerCase());

            if ((message.startsWith("-") && message.endsWith("-")) || (message.startsWith("▬") && message.endsWith("▬")) || (message.startsWith("≡") && message.endsWith("≡")) || (!message.contains(": ")) || (message.contains(mc.getSession().getUsername().toLowerCase()))) return;

            if (blockerRegex.matcher(message).find(0)) {
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
