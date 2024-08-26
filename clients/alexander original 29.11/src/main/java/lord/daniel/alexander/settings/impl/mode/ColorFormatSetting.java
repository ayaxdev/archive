package lord.daniel.alexander.settings.impl.mode;

import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.util.java.StringUtil;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Written by Daniel. on 17/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class ColorFormatSetting extends StringModeValue {

    public ColorFormatSetting(String name, AbstractModule owner, EnumChatFormatting enumChatFormatting) {
        super(name, owner, StringUtil.convertToReadableString(enumChatFormatting.getFriendlyName().toUpperCase()), getAvailableModes());
    }

    public static List<String> getAvailableModes() {
        List<String> modes = new ArrayList<>();
        for(EnumChatFormatting enumChatFormatting : EnumChatFormatting.values()) {
            if(enumChatFormatting == EnumChatFormatting.RESET)
                continue;
            modes.add(StringUtil.convertToReadableString(enumChatFormatting.getFriendlyName().toUpperCase()));
        }
        return modes;
    }

    public EnumChatFormatting getFormat() {
        for(EnumChatFormatting enumChatFormatting : EnumChatFormatting.values()) {
            if(StringUtil.convertToReadableString(enumChatFormatting.getFriendlyName().toUpperCase()).equals(getValue()))
                return enumChatFormatting;
        }
        return null;
    }

}
