package ja.tabio.argon.setting.impl;

import ja.tabio.argon.setting.Setting;

import java.util.Objects;

public class TextInputSetting extends Setting<String> {

    private String value;

    public TextInputSetting(String name, String displayName, String value) {
        super(name, displayName);

        this.value = value;
    }

    public TextInputSetting(String name, String value) {
        this(name, name, value);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        if (Objects.equals(this.value, value))
            return;

        final String oldValue = this.value;
        this.changeListener.onChange(true, oldValue, value);

        this.value = value;

        this.changeListener.onChange(false, oldValue, value);
    }
}
