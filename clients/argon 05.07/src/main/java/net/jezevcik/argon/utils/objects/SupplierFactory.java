package net.jezevcik.argon.utils.objects;

import net.jezevcik.argon.config.setting.Setting;

import java.util.function.Supplier;

public class SupplierFactory {

    public static Supplier<Boolean> setting(Setting<?> setting, boolean visibility, Object... values) {
        return () -> {
            for (Object value : values)
                if ((!visibility || setting.visible()) && setting.getValue().equals(value))
                    return true;

            return false;
        };
    }

    public static Supplier<Boolean> reverseSetting(Setting<?> setting, Object... values) {
        return () -> {
            for (Object value : values)
                if (setting.getValue().equals(value))
                    return false;

            return true;
        };
    }

}
