package lord.daniel.alexander.settings.impl.number;

import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.AbstractSetting;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class KeyBindValue extends AbstractSetting<Integer> {

    public KeyBindValue(String name, AbstractModule owner, Integer value) {
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

}
