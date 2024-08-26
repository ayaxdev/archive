package ja.tabio.argon.interfaces;

public interface Nameable {

    String getName();

    default String getDisplayName() {
        return getName();

    }

}
