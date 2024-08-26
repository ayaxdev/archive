package lord.daniel.alexander.settings.impl.mode;

import lombok.Getter;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.AbstractSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
public class MultiSelectValue extends AbstractSetting<List<String>> {

    private final ArrayList<String> values;

    public MultiSelectValue(String name, AbstractModule owner, String[] selected, String[] values) {
        super(name, owner, new ArrayList<>(Arrays.asList(selected)));
        this.values = new ArrayList<>(Arrays.asList(values));

        for(String s : values) {
            if(s.contains(":")) {
                throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public void setValueByString(String valueString) {
        this.getValue().clear();
        for(String string : valueString.split(":")) {
            this.getValue().add(string);
        }
    }

    @Override
    public String getValueAsString() {
        return String.join(":", this.getValue());
    }

    public void toggle(String value) {
        if(this.getValue().contains(value))
            this.getValue().remove(value);
        else
            this.getValue().add(value);
    }

    public boolean is(String string) {
        for(String s : getValue()) {
            if(s.equalsIgnoreCase(string))
                return true;
        }
        return getValue().contains(string);
    }

}
