package ja.tabio.argon.setting.impl;

import ja.tabio.argon.setting.Setting;

import java.util.Objects;

public class BooleanSetting extends Setting<Boolean> {

    private boolean value;

    public BooleanSetting(String name, String displayName, boolean value) {
        super(name, displayName);
        this.value = value;
    }

    public BooleanSetting(String name, boolean value) {
        super(name, name);
        this.value = value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        if (Objects.equals(this.value, value))
            return;

        final boolean oldValue = this.value;
        this.changeListener.onChange(true, oldValue, value);

        this.value = value;

        this.changeListener.onChange(false, oldValue, value);
    }

}
