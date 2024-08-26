package ja.tabio.argon.loader.impl;

import ja.tabio.argon.loader.Loader;

import java.util.LinkedList;
import java.util.List;

public class AsyncLoader implements Loader {

    private final List<Runnable> runnables = new LinkedList<>();
    private final List<Thread> threads = new LinkedList<>();

    @Override
    public void add(Runnable runnable) {
        runnables.add(runnable);
    }

    @Override
    public void run() {
        for (Runnable runnable : runnables)
            threads.add(new Thread(runnable));

        for (Thread thread : threads)
            thread.start();
    }

    @Override
    public boolean finished() {
        for (Thread thread : threads)
            if (thread.isAlive())
                return false;

        return true;
    }
}
