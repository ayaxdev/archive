package net.jezevcik.argon.config.setting.interfaces;

public interface ValueChangeRunnable<T> {

    void onChange(T newValue, T oldValue);

    default T override(T newValue, T oldValue) {
        return newValue;
    }

}
