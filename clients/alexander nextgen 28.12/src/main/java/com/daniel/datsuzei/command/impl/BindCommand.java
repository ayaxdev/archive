package com.daniel.datsuzei.command.impl;

import com.daniel.datsuzei.command.CommandTemplate;
import com.daniel.datsuzei.module.ModuleFeature;
import com.daniel.datsuzei.module.ModuleManager;
import com.daniel.datsuzei.util.chat.ChatUtil;
import org.lwjglx.input.Keyboard;

public class BindCommand extends CommandTemplate {

    public BindCommand() {
        super("Bind", "Binds a module", "<module> <key>", new String[]{"bind", "b"});
    }

    @Override
    public boolean run(String[] arguments) {
        if(arguments.length != 3)
            return false;

        final ModuleFeature module = ModuleManager.getSingleton().getByName(arguments[1].toLowerCase());
        final int key = Keyboard.getKeyIndex(arguments[2].toUpperCase());

        if(module != null) {
            module.setKey(key);
            ChatUtil.display(STR."Set \{module.getName()}'s key to \{Keyboard.getKeyName(key)}");
            return true;
        }

        return false;
    }

}
