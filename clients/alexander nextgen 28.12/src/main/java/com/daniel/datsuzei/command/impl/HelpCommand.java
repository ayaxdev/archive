package com.daniel.datsuzei.command.impl;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.command.CommandManager;
import com.daniel.datsuzei.command.CommandTemplate;
import com.daniel.datsuzei.util.chat.ChatUtil;

public class HelpCommand extends CommandTemplate {

    public HelpCommand() {
        super("Help", "Prints out this message", "", new String[]{"help", "h"});
    }

    @Override
    public boolean run(String[] arguments) {
        for(CommandTemplate commandTemplate : CommandManager.getSingleton().getFeatures()) {
            ChatUtil.display(STR."\{commandTemplate.name} - \{commandTemplate.description}");
            ChatUtil.display(STR."  Usage: .\{commandTemplate.name.toLowerCase()} \{commandTemplate.usage}");
        }
        return true;
    }
}
