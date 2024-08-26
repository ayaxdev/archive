package ja.tabio.argon.loader.impl;

import ja.tabio.argon.loader.Loader;

import java.util.LinkedList;
import java.util.List;

public class SyncLoader implements Loader {

    private final List<Runnable> runnables = new LinkedList<>();
    private volatile Thread thread;

    @Override
    public void add(Runnable runnable) {
        runnables.add(runnable);
    }

    @Override
    public void run() {
        this.thread = new Thread(() -> {
            for (Runnable runnable : runnables) {
                runnable.run();
            }
        });

        this.thread.start();
    }

    @Override
    public boolean finished() {
        return thread != null && !thread.isAlive();
    }
}
