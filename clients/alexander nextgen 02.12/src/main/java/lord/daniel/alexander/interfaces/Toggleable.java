package lord.daniel.alexander.interfaces;

/**
 * Written by Daniel. on 21/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public interface Toggleable {

    boolean isEnabled();

    void setEnabled(final boolean enabled);

    default void toggle() {
        setEnabled(!isEnabled());
    }

}
