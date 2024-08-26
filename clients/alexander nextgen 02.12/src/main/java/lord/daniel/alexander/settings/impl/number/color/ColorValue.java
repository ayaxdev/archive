package lord.daniel.alexander.settings.impl.number.color;

import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.AbstractSetting;
import lord.daniel.alexander.settings.impl.bool.ExpandableValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.storage.impl.FontStorage;
import lord.daniel.alexander.ui.elements.ColorPicker;

import java.awt.*;
import java.util.function.Supplier;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class ColorValue extends AbstractSetting<Color> {

    @Getter @Setter @Deprecated
    private float saturation = 1, brightness = 1, alpha = 1;

    @Getter
    private final ColorPicker colorPicker;

    private NumberValue<Integer> alphaValue;

    public ColorValue(String name, AbstractModule owner, Color value) {
        super(name, owner, value);
        this.colorPicker = new ColorPicker(this);

        alphaValue = new NumberValue<Integer>(name + "Alpha", owner, value.getAlpha(), 0, 255).addValueChangeListeners(((setting, oldValue, newValue) -> setValue(getValue())));
    }

    @Override
    public void setValue(Color color) {
        super.setValue(new Color(color.getRed(), color.getGreen(), color.getBlue(), alphaValue.getValue()));
    }

    @Override
    public void setValueByString(String valueString) {
        String[] split = valueString.split(":");
        setValue(new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), alphaValue.getValue()));
    }

    @Override
    public String getValueAsString() {
        return getValue().getRed() + ":" + getValue().getGreen() + ":" + getValue().getBlue();
    }

    @Override
    public <O extends AbstractSetting<Color>> O addVisibleCondition(Supplier<Boolean> visible) {
        this.visible.add(visible);
        alphaValue.addVisibleCondition(visible);
        return (O) this;
    }

    @Override
    public <O extends AbstractSetting<Color>> O addExpandableParents(ExpandableValue... expandableValues) {
        super.addExpandableParents(expandableValues);
        alphaValue.addExpandableParents(expandableValues);
        return (O) this;
    }

}
