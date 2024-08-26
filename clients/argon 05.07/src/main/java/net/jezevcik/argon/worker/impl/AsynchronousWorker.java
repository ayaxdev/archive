package net.jezevcik.argon.worker.impl;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.worker.Worker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Runs all tasks in a different thread.
 */
public class AsynchronousWorker extends Worker {

    /**
     * The list of created threads.
     */
    private final List<Thread> threads = new ArrayList<>();

    public AsynchronousWorker(String name, Object... waiting) {
        super(name, waiting);
    }

    @Override
    public synchronized void run() {
        tasks.forEach(runnable -> {
            final Thread thread = new Thread(() -> {
                try {
                    runnable.run();
                } catch (Exception e) {
                    ParekClient.LOGGER.error("Worker {} has crashed", name, e);
                    crashed = true;
                }
            });

            thread.start();

            threads.add(thread);
        });

        new Thread(() -> {
            while (!finished || crashed) {
                final Iterator<Thread> iterator = threads.iterator();

                if (!iterator.hasNext())
                    break;

                while (iterator.hasNext()) {
                    if (!iterator.next().isAlive())
                        iterator.remove();
                }
            }

            finished = true;

            ParekClient.LOGGER.info("Worker {} finished!", name);

            for (Object o : waiting) {
                synchronized (o) {
                    o.notify();
                }
            }
        }).start();
    }

}
