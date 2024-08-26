package lord.daniel.alexander.settings.impl.mode;

import lombok.Getter;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.util.array.ArrayUtils;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
public class MultiSelectNumberValue extends MultiSelectValue {


    public MultiSelectNumberValue(String name, AbstractModule owner, int[] selected, int minValue, int maxValue) {
        super(name, owner, ArrayUtils.intArrayToStringArray(selected), ArrayUtils.intArrayToStringArray(ArrayUtils.generateIntArrayInRange(minValue, maxValue)));
    }

    public boolean is(int value) {
        return getValue().contains(Integer.toString(value));

    }

}
