package com.daniel.datsuzei.command;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.event.impl.ChatEvent;
import com.daniel.datsuzei.feature.Manager;
import com.daniel.datsuzei.module.ModuleManager;
import com.daniel.datsuzei.util.chat.ChatUtil;
import com.github.jezevcik.eventbus.Listener;
import com.github.jezevcik.eventbus.annotations.Listen;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.InvocationTargetException;

public class CommandManager extends Manager<CommandTemplate> {

    private static volatile CommandManager commandManager;

    public static CommandManager getSingleton() {
        if(commandManager == null)
            commandManager = new CommandManager();

        return commandManager;
    }


    public CommandManager() {
        super(CommandTemplate.class);
    }

    @Override
    public void postMinecraftLaunch() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super.postMinecraftLaunch();

        DatsuzeiClient.getSingleton().getEventBus().subscribe(this);
    }

    @Listen
    public final Listener<ChatEvent> chatEventListener = chatEvent -> {
        final String message = chatEvent.message;

        if(message.startsWith(String.valueOf(DatsuzeiClient.COMMAND_EXECUTE_PREFIX))) {
            chatEvent.setCancelled(true);

            final String commandMessage = message.substring(1);
            final String[] split = commandMessage.split(" ");

            for(CommandTemplate commandTemplate : getFeatures()) {
                if(ArrayUtils.contains(commandTemplate.triggers, split[0])) {
                    if(!commandTemplate.run(split)) {
                        ChatUtil.display(STR."Usage: .\{commandTemplate.name.toLowerCase()} \{commandTemplate.usage}");
                    }

                    return;
                }
            }

            ChatUtil.display("Command not found");
        }
    };

}
