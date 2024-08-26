package lord.daniel.alexander.settings.impl.number.color;

import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.impl.hud.ColorModule;
import lord.daniel.alexander.settings.AbstractSetting;
import lord.daniel.alexander.settings.impl.bool.ExpandableValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.util.render.color.ColorUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
public class ClientColorValue extends AbstractSetting<Color> {

    private final boolean allowSync;

    @Setter
    private boolean sync;

    private final List<AbstractSetting<?>> values;

    public ClientColorValue(String name, AbstractModule owner) {
        this(name, owner, new Color(178, 216, 236));
    }

    public ClientColorValue(String name, AbstractModule owner, Color color) {
        this(name, owner, color, true, true);
    }

    public ClientColorValue(String name, AbstractModule owner, boolean sync) {
        this(name, owner, new Color(178, 216, 236), sync, true);
    }

    public ClientColorValue(String name, AbstractModule owner, Color color, boolean sync, boolean allowSync) {
        super(name, owner, null);

        this.sync = sync;
        this.allowSync = allowSync;

        final StringModeValue colorMode = new StringModeValue(name + "Mode", owner, "Static", new String[]{"Static", "Fade", "Blend", "Astolfo", "Rainbow"});
        final ColorValue main = new ColorValue(name + "Main", owner, color).addVisibleCondition(() -> !colorMode.is("Rainbow"));
        final ColorValue secondary = new ColorValue(name + "Secondary", owner, new Color(178, 216, 236)).addVisibleCondition(() -> colorMode.is("Blend"));
        final NumberValue<Float> darkFactor = new NumberValue<>(name + "FadeDarkFactor", owner, 0.49F, 0F, 1F, 2).addVisibleCondition(() -> colorMode.is("Fade"));
        values = Arrays.asList(colorMode, main, secondary, darkFactor);

        for(AbstractSetting<?> abstractSetting : values) {
            abstractSetting.addVisibleCondition(() -> !isSync());
            for(Supplier<Boolean> supplier : this.visible) {
                abstractSetting.addVisibleCondition(supplier);
            }
        }
    }

    @Override
    public <O extends AbstractSetting<Color>> O addVisibleCondition(Supplier<Boolean> visible) {
        this.visible.add(visible);
        for(AbstractSetting<?> abstractSetting : values) {
            abstractSetting.addVisibleCondition(visible);
        }
        return (O) this;
    }

    @Override
    public <O extends AbstractSetting<Color>> O addExpandableParents(ExpandableValue... expandableValues) {
        super.addExpandableParents(expandableValues);
        for(AbstractSetting<?> abstractSetting : values) {
            abstractSetting.addExpandableParents(expandableValues);
        }
        return (O) this;
    }

    @Override
    public Color getValue() {
        if(isSync()) {
            return ColorModule.getClientColor();
        } else {
            return getColorFromSettings(this.getOwner(), 1);
        }
    }

    public Color getValue(int counter) {
        if(isSync()) {
            return ColorModule.getClientColor(counter);
        } else {
            return getColorFromSettings(this.getOwner(), counter);
        }
    }

    public Color getColorFromSettings(AbstractModule owner, int counter) {
        String colorMode = owner.getSettingByName(getName() + "Mode").getValueAsString();
        Color mainColor = (Color) owner.getSettingByName(getName() + "Main").getValue();
        Color secondaryColor = (Color) owner.getSettingByName(getName() + "Secondary").getValue();
        float darkFactor = (float) owner.getSettingByName(getName() + "FadeDarkFactor").getValue();

        return switch (colorMode) {
            default -> mainColor;
            case "Fade" -> new Color(ColorUtil.fadeBetween(mainColor.getRGB(), ColorUtil.darken(mainColor.getRGB(), darkFactor), counter * 150L));
            case "Blend" -> new Color(ColorUtil.fadeBetween(mainColor.getRGB(), secondaryColor.getRGB(), counter * 150L));
            case "Rainbow" -> new Color(ColorUtil.getRainbow(3000, (int) (counter * 150L)));
            case "Astolfo" -> new Color(ColorUtil.blendRainbowColours(counter * 150L));
        };
    }

    @Override
    public void setValueByString(String valueString) {
        setSync(Boolean.parseBoolean(valueString));
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(isSync());
    }

}
