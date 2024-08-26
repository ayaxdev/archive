package ja.tabio.argon.loader;

public interface Loader {

    void add(Runnable runnable);

    void run();

    boolean finished();

}
