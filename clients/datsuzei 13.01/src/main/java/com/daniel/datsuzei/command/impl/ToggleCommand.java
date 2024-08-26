package com.daniel.datsuzei.command.impl;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.command.CommandTemplate;
import com.daniel.datsuzei.module.ModuleFeature;
import com.daniel.datsuzei.module.ModuleManager;
import com.daniel.datsuzei.util.chat.ChatUtil;

public class ToggleCommand extends CommandTemplate {

    public ToggleCommand() {
        super("Toggle", "Toggles a module", "<module>", new String[]{"toggle", "t"});
    }

    @Override
    public boolean run(String[] arguments) {
        if(arguments.length != 2)
            return false;

        final ModuleFeature module = ModuleManager.getSingleton().getByName(arguments[1].toLowerCase());

        if(module != null) {
            module.toggleEnabled();
            ChatUtil.display(STR."\{module.isEnabled() ? "Enabled" : "Disabled"} \{module.getName()}!");
            return true;
        }

        return false;
    }

}
