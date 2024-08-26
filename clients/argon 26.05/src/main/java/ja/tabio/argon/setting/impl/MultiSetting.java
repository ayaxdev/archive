package ja.tabio.argon.setting.impl;

import ja.tabio.argon.setting.Setting;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MultiSetting extends Setting<List<String>> {

    private List<String> value;
    public final String[] modes;

    public MultiSetting(String name, String displayName, String[] value, String[] modes) {
        super(name, displayName);
        this.value = new ArrayList<>(Arrays.asList(value));
        this.modes = modes;
    }

    public MultiSetting(String name, String[] value, String[] modes) {
        super(name, name);
        this.value = new ArrayList<>(Arrays.asList(value));
        this.modes = modes;
    }

    public int toggle(String value) {
        if (!ArrayUtils.contains(modes, value))
            return -1;

        final List<String> old = this.value;

        if (this.value.contains(value)) {
            this.changeListener.onChange(true, old, this.value);

            this.value.remove(value);

            this.changeListener.onChange(false, old, this.value);

            return 0;
        } else {
            this.changeListener.onChange(true, old, this.value);

            this.value.add(value);

            this.changeListener.onChange(false, old, this.value);

            return 1;
        }
    }

    public boolean isEnabled(String value) {
        return this.value.contains(value);
    }

    @Override
    public List<String> getValue() {
        return value;
    }

    @Override
    public void setValue(List<String> value) {
        if (Objects.equals(this.value, value))
            return;

        final List<String> oldValue = this.value;
        this.changeListener.onChange(true, oldValue, value);

        this.value = value;

        this.changeListener.onChange(false, oldValue, value);
    }

}
