package com.skidding.atlas.module.impl.chat;

import com.skidding.atlas.event.impl.game.RunTickEvent;
import com.skidding.atlas.event.impl.game.LoadWorldEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.processor.ProcessorManager;
import com.skidding.atlas.processor.impl.PasswordProcessor;
import com.skidding.atlas.util.minecraft.chat.ChatUtil;
import com.skidding.atlas.util.system.TimerUtil;
import io.github.racoondog.norbit.EventHandler;

import java.util.Map;

public class AutoLoginModule extends ModuleFeature {

    public AutoLoginModule() {
        super(new ModuleBuilder("AutoLogin", "Automatically logins with saved passwords", ModuleCategory.CHAT));
    }

    final PasswordProcessor processor = ProcessorManager.getSingleton().getByClass(PasswordProcessor.class);
    final TimerUtil timerUtil = new TimerUtil();
    private boolean sent = false;
    private String lastIp;

    @EventHandler
    public final void onTick(RunTickEvent runTickEvent) {
        if(mc.theWorld == null || mc.thePlayer == null || mc.getCurrentServerData() == null) {
            sent = false;
            lastIp = null;
            return;
        }

        final String serverIP = mc.getCurrentServerData().serverIP;

        if(lastIp == null || !lastIp.equals(serverIP)) {
            timerUtil.reset();
            sent = false;
        }

        lastIp = serverIP;

        if(!timerUtil.hasElapsed(500) || sent)
            return;

        if(processor.savedPasswordsMap.containsKey(serverIP)) {
            final Map<String, String> currentServerMap = processor.savedPasswordsMap.get(serverIP);

            if(currentServerMap.containsKey(mc.session.getUsername())) {
                final String password = currentServerMap.get(mc.session.getUsername());
                ChatUtil.print("(AutoLogin) Attempting login!");
                mc.thePlayer.sendChatMessage(STR."/login \{password}");

                sent = true;
            } else {
                ChatUtil.print("(AutoLogin) No data found for current user!");
                sent = true;
            }
        } else {
            ChatUtil.print("(AutoLogin) No data found for current server!");
            sent = true;
        }
    }

    @EventHandler
    public final void onWorld(LoadWorldEvent loadWorldEvent) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

}
