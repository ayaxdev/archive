package com.daniel.datsuzei.command.impl;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.command.CommandTemplate;
import com.daniel.datsuzei.util.chat.ChatUtil;

public class AboutCommand extends CommandTemplate {

    public AboutCommand() {
        super("About", "Displays client information", "", new String[]{"about", "info", "a", "i"});
    }

    @Override
    public boolean run(String[] arguments) {
        ChatUtil.display(STR."\{DatsuzeiClient.NAME} v\{DatsuzeiClient.VERSION}");
        return true;
    }
}
