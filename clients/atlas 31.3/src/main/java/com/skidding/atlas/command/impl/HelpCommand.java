package com.skidding.atlas.command.impl;

import com.skidding.atlas.command.Command;
import com.skidding.atlas.command.CommandManager;
import com.skidding.atlas.util.minecraft.chat.ChatUtil;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "Shows all commands", "", new String[]{"help", "h"});
    }

    @Override
    protected boolean execute(String[] args) {
        ChatUtil.print("Available commands:");

        for(Command command : CommandManager.getSingleton().getFeatures()) {
            ChatUtil.print(STR."\{command.getName()}:");
            ChatUtil.print(STR." - Usage: .\{command.getName()} \{command.usage}");
            ChatUtil.print(STR." - Triggers: \{String.join(", ", command.triggers)}");
            ChatUtil.print(STR." - Description: \{command.getDescription()}");
        }

        return true;
    }

}
