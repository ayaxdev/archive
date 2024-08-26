package com.skidding.atlas.command;

import com.skidding.atlas.event.impl.player.chat.ChatEvent;
import com.skidding.atlas.feature.Manager;
import com.skidding.atlas.util.minecraft.chat.ChatUtil;
import de.florianmichael.rclasses.common.array.ArrayUtils;
import io.github.racoondog.norbit.EventHandler;

public final class CommandManager extends Manager<Command> {

    private static volatile CommandManager commandManager;

    public static synchronized CommandManager getSingleton() {
        return commandManager == null ? commandManager = new CommandManager() : commandManager;
    }

    public CommandManager() {
        super(Command.class);
    }

    @EventHandler
    public void onChat(ChatEvent chatEvent) {
        chatEvent.cancelled = run(chatEvent.message);
    }

    public boolean run(String message) {
        if(!message.startsWith("."))
            return false;

        final String important = message.substring(1);
        final String[] split = important.split(" ");

        Command foundCommand = null;
        for(Command command : getFeatures()) {
            if(ArrayUtils.contains(command.triggers, split[0])) {
                foundCommand = command;
                break;
            }
        }

        if(foundCommand != null)
            foundCommand.run(split);
        else {
            ChatUtil.print("Command not found");
            ChatUtil.print("Consider trying '.help'");
        }

        return true;
    }

}
