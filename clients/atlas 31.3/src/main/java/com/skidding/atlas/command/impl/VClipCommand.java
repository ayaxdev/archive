package com.skidding.atlas.command.impl;

import com.skidding.atlas.command.Command;
import com.skidding.atlas.util.minecraft.chat.ChatUtil;

public class VClipCommand extends Command {

    public VClipCommand() {
        super("vclip", "Clip up or down","<blocks>", new String[]{"vclip", "vc"});
    }

    @Override
    protected boolean execute(String[] args) {
        if (args.length < 2) {
            return false;
        }

        try {
            double blocks = Double.parseDouble(args[1]);
            getPlayer().setPosition(getPlayer().posX, getPlayer().posY + blocks, getPlayer().posZ);
            String direction = (blocks < 0.0D) ? "down" : "up";
            ChatUtil.print(STR."Clipped \{blocks} blocks \{direction}.");
        } catch (NumberFormatException e) {
            ChatUtil.print("Input must be a number!");
            return false;
        }

        return true;
    }

}
