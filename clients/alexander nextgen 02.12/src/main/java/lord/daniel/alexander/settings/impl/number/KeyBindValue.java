package lord.daniel.alexander.settings.impl.number;

import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.AbstractSetting;
import lord.daniel.alexander.util.array.ArrayUtils;
import org.lwjglx.input.Keyboard;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class KeyBindValue extends AbstractSetting<Integer> {

    public static final Integer[] UNBIND_KEYS = {Keyboard.KEY_BACK, Keyboard.KEY_RETURN, Keyboard.KEY_GRAVE, Keyboard.KEY_ESCAPE};

    public KeyBindValue(String name, AbstractModule owner, int value) {
        super(name, owner, value);
    }

    @Override
    public void setValueByString(String valueString) {
        setValue(Integer.valueOf(valueString));
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(getValue());
    }

    @Override
    public void setValue(Integer integer) {
        if(ArrayUtils.contains(UNBIND_KEYS, integer)) {
            super.setValue(0);
            return;
        }
        super.setValue(integer);
    }

}
