package lord.daniel.alexander.settings.impl.bool;

import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.AbstractSetting;
import lord.daniel.alexander.settings.impl.bool.util.BooleanParser;

import java.util.Optional;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class BooleanValue extends AbstractSetting<Boolean> {

    public BooleanValue(String name, AbstractModule owner, Boolean value) {
        super(name, owner, value);
    }

    @Override
    public void setValueByString(String valueString) {
        Optional<Boolean> result = BooleanParser.parse(valueString);
        result.ifPresent(super::setValue);
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(getValue());
    }

}
