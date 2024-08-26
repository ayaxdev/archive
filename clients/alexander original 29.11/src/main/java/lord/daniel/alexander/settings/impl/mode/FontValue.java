package lord.daniel.alexander.settings.impl.mode;

import lombok.Getter;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.AbstractSetting;
import lord.daniel.alexander.settings.impl.bool.ExpandableValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.storage.impl.FontStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
public class FontValue extends AbstractSetting<FontRenderer> {

    private final List<AbstractSetting<?>> values = new ArrayList<>();

    public FontValue(String name, AbstractModule owner) {
        this(name, owner, "Roboto", "Regular", 19);
    }

    public FontValue(String name, AbstractModule owner, String family, String type, int size) {
        super(name, owner, null);

        final List<String> modes = new ArrayList<>();

        modes.add("Minecraft");

        for(Map.Entry<String, List<String>> fontEntry : FontStorage.getFontStorage().getSavedFontTypes().entrySet()) {
            modes.add(fontEntry.getKey());
        }

        StringModeValue familyValue = new StringModeValue(name + "Family", getOwner(), family, modes);

        values.add(familyValue);

        for(Map.Entry<String, List<String>> fontEntry : FontStorage.getFontStorage().getSavedFontTypes().entrySet()) {
            values.add(new StringModeValue(name + fontEntry.getKey() + "Type", getOwner(), type, fontEntry.getValue()).addVisibleCondition(() -> familyValue.is(fontEntry.getKey())));
        }

        values.add(new NumberValue<>(name + "Size", getOwner(), size, 10, 50));

        for(AbstractSetting<?> abstractSetting : values) {
            for(Supplier<Boolean> supplier : this.visible) {
                abstractSetting.addVisibleCondition(supplier);
            }
        }
    }

    @Override
    public <O extends AbstractSetting<FontRenderer>> O addVisibleCondition(Supplier<Boolean> visible) {
        this.visible.add(visible);
        for(AbstractSetting<?> abstractSetting : values) {
            abstractSetting.addVisibleCondition(visible);
        }
        return (O) this;
    }

    @Override
    public <O extends AbstractSetting<FontRenderer>> O addExpandableParents(ExpandableValue... expandableValues) {
        super.addExpandableParents(expandableValues);
        for(AbstractSetting<?> abstractSetting : values) {
            abstractSetting.addExpandableParents(expandableValues);
        }
        return (O) this;
    }

    @Override
    public FontRenderer getValue() {
        String family = getOwner().getSettingByName(getName() + "Family").getValueAsString();
        if(family.equals("Minecraft"))
            return Minecraft.getMinecraft().fontRendererObj;
        String type = getOwner().getSettingByName(getName() + family + "Type").getValueAsString();
        float size = (int) getOwner().getSettingByName(getName() + "Size").getValue();
        return FontStorage.getFontStorage().get(family, type, size);
    }

    @Override
    public void setValueByString(String valueString) { }

    @Override
    public String getValueAsString() {
        return null;
    }

}
