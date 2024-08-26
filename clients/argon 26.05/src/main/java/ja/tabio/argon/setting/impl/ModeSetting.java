package ja.tabio.argon.setting.impl;

import ja.tabio.argon.Argon;
import ja.tabio.argon.setting.Setting;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Objects;

public class ModeSetting extends Setting<String> {

    private String value;
    public final String[] modes;

    public ModeSetting(String name, String displayName, String value, String[] modes) {
        this(name, displayName, value, (Object[]) modes);
    }

    public ModeSetting(String name, String value, String... modes) {
        this(name, name, value, modes);
    }

    public ModeSetting(String name, String displayName, String value, Object[] modes) {
        super(name, displayName);

        this.modes = new String[modes.length];

        for (int i = 0; i < modes.length; i++) {
            this.modes[i] = modes[i].toString();
        }

        try {
            this.setValue(value);
        } catch (Exception e) {
            Argon.getInstance().logger.error("Invalid default value! {} {}", name, value);
        }
    }

    public ModeSetting(String name, String value, Object... modes) {
        this(name, name, value, modes);
    }

    public boolean is(String mode) {
        return getValue().equals(mode);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        if (Objects.equals(this.value, value))
            return;

        if (ArrayUtils.contains(modes, value)) {
            final String oldValue = this.value;
            this.changeListener.onChange(true, oldValue, value);

            this.value = value;

            this.changeListener.onChange(false, oldValue, value);
        } else {
            throw new IllegalArgumentException(String.format("Invalid mode value '%s'!", value));
        }
    }

}
