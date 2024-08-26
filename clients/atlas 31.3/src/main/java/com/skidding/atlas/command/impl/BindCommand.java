package com.skidding.atlas.command.impl;

import com.skidding.atlas.command.Command;
import com.skidding.atlas.keybind.KeybindingFeature;
import com.skidding.atlas.keybind.KeybindingManager;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.module.ModuleManager;
import com.skidding.atlas.util.minecraft.chat.ChatUtil;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Command {

    public BindCommand() {
        super("bind", "Binds a key to a command","<module> <key/clear/print>", new String[]{"bind", "b"});
    }

    @Override
    protected boolean execute(String[] args) {
        if(args.length < 3) {
            return false;
        }

        final ModuleFeature moduleFeature = ModuleManager.getSingleton().getByName(args[1]);

        if(moduleFeature == null) {
            return false;
        }

        switch (args[2]) {
            case "clear", "del", "delete", "rem", "remove" -> {
                KeybindingManager.getSingleton().getByName(STR."Module:\{moduleFeature.getName()}").key = 0;

                ChatUtil.print(STR."Module \{moduleFeature.getName()} was unbound");
            }
            case "print", "list" -> {
                final KeybindingFeature keybindingFeature = KeybindingManager.getSingleton().getByName(STR."Module:\{moduleFeature.getName()}");
                if (keybindingFeature.key == 0) {
                    ChatUtil.print(STR."Module \{moduleFeature.getName()} is not bound");
                } else {
                    ChatUtil.print(STR."Module \{moduleFeature.getName()} is bound to \{Keyboard.getKeyName(keybindingFeature.key)} - \{keybindingFeature.key}");
                }
            }
            default -> {
                final KeybindingFeature keybindingFeature = KeybindingManager.getSingleton().getByName(STR."Module:\{moduleFeature.getName()}");
                final String key = args[2];

                keybindingFeature.key = Keyboard.getKeyIndex(key.toUpperCase());

                if(keybindingFeature.key == 0)
                    ChatUtil.print(STR."Module \{moduleFeature.getName()} was unbound");
                else
                    ChatUtil.print(STR."Module \{moduleFeature.getName()} was bound to \{Keyboard.getKeyName(keybindingFeature.key)} - \{keybindingFeature.key}");
            }
        }

        return true;
    }

}
