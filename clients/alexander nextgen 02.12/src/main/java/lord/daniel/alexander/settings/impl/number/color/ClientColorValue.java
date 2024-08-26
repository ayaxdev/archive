package lord.daniel.alexander.settings.impl.number.color;

import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.AbstractSetting;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.bool.ExpandableValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.util.render.color.ColorUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ClientColorValue extends AbstractSetting<Color> {

    private final List<AbstractSetting<?>> values = new ArrayList<>();

    private final StringModeValue mode, colorPattern;
    private final ColorValue firstColor, secondColor;
    private final NumberValue<Float> darkFactor;
    private final BooleanValue overrideFinalAlpha;
    private final NumberValue<Integer> finalAlpha;

    public ClientColorValue(String name, AbstractModule owner) {
        this(name, owner, true);
    }

    public ClientColorValue(String name, AbstractModule owner, Color color) {
        this(name, owner, color, false);
    }

    public ClientColorValue(String name, AbstractModule owner, boolean sync) {
        this(name, owner, ColorUtil.DEFAULT_COLOR, sync);
    }

    public ClientColorValue(String name, AbstractModule owner, Color color, boolean sync) {
        super(name, owner, ColorUtil.DEFAULT_COLOR);

        values.add(mode = new StringModeValue(name + "Mode", owner, "Static", new String[]{"Static", "Blend", "Fade"}));
        values.add(colorPattern = new StringModeValue(name + "Pattern", owner, "Custom", new String[] {"Custom", "SkyRainbow", "Czechia", "Germany"}).addVisibleCondition(mode, "Blend"));
        values.add(firstColor = new ColorValue(name, owner, color).addVisibleCondition(() -> !mode.is("Blend") || (colorPattern.is("Custom"))));
        values.add(secondColor = new ColorValue(name + "Secondary", owner, color.darker()).addVisibleCondition(mode, "Blend").addVisibleCondition(colorPattern, "Custom"));
        values.add(darkFactor = new NumberValue<Float>(name + "DarkFactor", owner, 1f, 0f, 2f, 2).addVisibleCondition(mode,"Fade"));
        values.add(overrideFinalAlpha = new BooleanValue(name + "OverrideFinalAlpha", owner, false).addVisibleCondition(mode, false, "Static"));
        values.add(finalAlpha = new NumberValue<>(name + "FinalAlpha", owner, 255, 0, 255).addVisibleCondition(overrideFinalAlpha).addVisibleCondition(mode, false, "Static"));
    }

    @Override
    public Color getValue() {
        return getValue(1);
    }

    public Color getValue(int counter) {
        Color color = switch (mode.getValue()) {
            case "Blend" -> {
                if(!this.colorPattern.is("Custom")) {
                    int[] colorPattern = switch (this.colorPattern.getValue()) {
                        case "Czechia" -> ColorUtil.CZECHIA_COLORS;
                        case "Germany" -> ColorUtil.GERMAN_COLORS;
                        default -> ColorUtil.SKY_RAINBOW_COLORS;
                    };
                    yield new Color(ColorUtil.blendColours(colorPattern, ColorUtil.getFadingFromSysTime(counter * 150L)));
                } else {
                    yield new Color(ColorUtil.fadeBetween(firstColor.getValue().getRGB(), secondColor.getValue().getRGB(), counter * 150L));
                }
            }
            case "Fade" -> new Color(ColorUtil.fadeBetween(firstColor.getValue().getRGB(), ColorUtil.darken(secondColor.getValue().getRGB(), darkFactor.getValue()), counter * 150L));
            default -> firstColor.getValue();
        };

        if(overrideFinalAlpha.getValue()) {
            color = ColorUtil.setAlpha(color, finalAlpha.getValue());
        }

        super.setValue(color);

        return color;
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
    public void setValueByString(String valueString) {

    }

    @Override
    public String getValueAsString() {
        return null;
    }
}
