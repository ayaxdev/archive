package net.jezevcik.argon.worker.impl;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.worker.Worker;

/**
 * Runs all tasks in a single thread.
 */
public class SynchronousWorker extends Worker {

    private Thread thread;

    public SynchronousWorker(String name, Object... waiting) {
        super(name, waiting);
    }

    @Override
    public synchronized void run() {
        this.thread = new Thread(() -> {
            try {
                for (Runnable task : tasks) {
                    task.run();
                }

                finished = true;

                ParekClient.LOGGER.info("Worker {} finished!", name);

                for (Object o : waiting) {
                    o.notify();
                }
            } catch (Exception e) {
                ParekClient.LOGGER.error("Worker {} has crashed", name, e);
                this.crashed = true;

            }
        });

        this.thread.start();
    }

}
