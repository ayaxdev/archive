package ja.tabio.argon.interfaces;

public interface IToggleable {

    default void changeEnabled() {
        setEnabled(!isEnabled());
    }

    void setEnabled(boolean enabled);

    boolean isEnabled();

}
