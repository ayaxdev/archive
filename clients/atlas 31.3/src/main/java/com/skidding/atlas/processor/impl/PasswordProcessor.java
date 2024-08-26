package com.skidding.atlas.processor.impl;

import com.skidding.atlas.event.impl.network.HandlePacketEvent;
import com.skidding.atlas.processor.Processor;
import de.florianmichael.rclasses.common.array.ArrayUtils;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.network.play.client.C01PacketChatMessage;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PasswordProcessor extends Processor {

    public static final String[] REGISTER_TRIGGERS = new String[] {
            "/register", "/reg"
    };

    public static final String[] LOGIN_TRIGGERS = new String[] {
            "/login"
    };

    public final Map<String, Map<String, String>> savedPasswordsMap = new LinkedHashMap<>();

    @EventHandler(priority = 9999)
    public void onPacket(HandlePacketEvent handlePacketEvent) {
        if(mc.thePlayer == null || mc.theWorld == null || mc.getCurrentServerData() == null)
            return;

        final String serverIP = mc.getCurrentServerData().serverIP;

        if(serverIP == null)
            return;

        if(!savedPasswordsMap.containsKey(serverIP) || savedPasswordsMap.get(serverIP) == null) {
            savedPasswordsMap.remove(serverIP);

            savedPasswordsMap.put(serverIP, new LinkedHashMap<>());
        }

        if(handlePacketEvent.packet instanceof C01PacketChatMessage c01PacketChatMessage) {
            final String message = c01PacketChatMessage.getMessage();
            final String lowercase = message.toLowerCase();

            final String[] split = message.split(" ");
            final String[] splitLowercase = lowercase.split(" ");

            if(split.length > 1) {
                if(split.length > 2 && ArrayUtils.contains(REGISTER_TRIGGERS, splitLowercase[0])) {
                    final String firstPassword = split[1],
                            secondPassword = split[2];

                    if(firstPassword.equals(secondPassword)) {
                        final Map<String, String> saved = savedPasswordsMap.get(serverIP);

                        if(!saved.containsKey(mc.getSession().getUsername()))
                            saved.put(mc.session.getUsername(), firstPassword);
                    }
                } else if(ArrayUtils.contains(LOGIN_TRIGGERS, splitLowercase[0])) {
                    final String password = split[1];

                    final Map<String, String> saved = savedPasswordsMap.get(serverIP);

                    if(!saved.containsKey(mc.getSession().getUsername())) {
                        saved.put(mc.session.getUsername(), password);
                    }
                }
            }
        }
    }

}
