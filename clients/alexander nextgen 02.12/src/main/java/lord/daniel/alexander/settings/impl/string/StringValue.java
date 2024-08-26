package lord.daniel.alexander.settings.impl.string;

import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.AbstractSetting;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class StringValue extends AbstractSetting<String> {

    public StringValue(String name, AbstractModule owner, String value) {
        super(name, owner, value);
    }

    @Override
    public void setValueByString(String valueString) {
        super.setValue(valueString);
    }

    @Override
    public String getValueAsString() {
        return getValue();
    }

}
