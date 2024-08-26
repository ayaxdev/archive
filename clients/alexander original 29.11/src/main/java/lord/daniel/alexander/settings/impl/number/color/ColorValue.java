package lord.daniel.alexander.settings.impl.number.color;

import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.AbstractSetting;

import java.awt.*;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class ColorValue extends AbstractSetting<Color> {

    @Getter @Setter
    private float saturation = 1, brightness = 1, alpha = 1;

    public ColorValue(String name, AbstractModule owner, Color value) {
        super(name, owner, value);
    }

    @Override
    public void setValueByString(String valueString) {
        String[] split = valueString.split(":");
        setValue(new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])));
    }

    @Override
    public String getValueAsString() {
        return getValue().getRed() + ":" + getValue().getGreen() + ":" + getValue().getBlue() + ":" + getValue().getAlpha();
    }

}
