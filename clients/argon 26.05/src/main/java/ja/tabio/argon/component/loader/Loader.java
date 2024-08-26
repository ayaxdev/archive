package ja.tabio.argon.component.loader;

public interface Loader {

    void add(Runnable runnable);

    void run();

    boolean finished();

}
