package ja.tabio.argon.interfaces;

public interface INameable {

    String getName();

    default String getDisplayName() {
        return getName();

    }

}
