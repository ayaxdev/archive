package lord.daniel.alexander.settings.interfaces;

import lord.daniel.alexander.settings.AbstractSetting;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public interface IValueChangeListener<T> {

    void onValueChange(AbstractSetting setting, T oldValue, T newValue);

}
