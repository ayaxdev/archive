package lord.daniel.alexander.settings.impl.mode;

import lombok.Getter;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.AbstractSetting;

import java.util.Arrays;
import java.util.List;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
public class StringModeValue extends AbstractSetting<String> {

    private List<String> modes;

    public StringModeValue(String name, AbstractModule owner, String value, String[] modes) {
        super(name, owner, value);
        this.modes = Arrays.asList(modes);
    }

    public StringModeValue(String name, AbstractModule owner, String value, List<String> modes) {
        super(name, owner, value);
        this.modes = modes;
    }

    public StringModeValue(String name, AbstractModule owner, String[] modes, String value) {
        super(name, owner, value);
        this.modes = Arrays.asList(modes);
    }

    public StringModeValue(String name, AbstractModule owner, List<String> modes, String value) {
        super(name, owner, value);
        this.modes = modes;
    }


    @Override
    public void setValueByString(String valueString) {
        if(modes.contains(valueString))
            this.setValue(valueString);
        else
            this.setValue(modes.get(0));
    }

    @Override
    public String getValueAsString() {
        return this.getValue();
    }

    public boolean is(String string) {
        return getValue().equalsIgnoreCase(string);
    }

}
