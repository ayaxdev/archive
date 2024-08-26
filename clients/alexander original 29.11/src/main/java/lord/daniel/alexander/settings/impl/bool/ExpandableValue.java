package lord.daniel.alexander.settings.impl.bool;

import lombok.Getter;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.AbstractSetting;
import lord.daniel.alexander.settings.impl.bool.util.BooleanParser;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class ExpandableValue extends AbstractSetting<Boolean> {

    @Getter
    private final ArrayList<AbstractSetting<?>> values = new ArrayList<>();

    public ExpandableValue(String name, AbstractModule owner) {
        super(name, owner, false);
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
