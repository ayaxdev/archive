package ja.tabio.argon.interfaces;

public interface Toggleable {

    default void changeEnabled() {
        setEnabled(!isEnabled());
    }

    void setEnabled(boolean enabled);

    boolean isEnabled();

}
