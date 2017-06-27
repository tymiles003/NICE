package com.sudicode.nice;

/**
 * Utility class.
 */
public class Util {

    /**
     * Illegal.
     */
    private Util() {
    }

    /**
     * Run a task in the background. The task will be run in a <strong>daemon</strong> thread. Not recommended for
     * tasks which require resource cleanup.
     *
     * @param task A {@link Runnable} containing the task to run
     */
    public static void submitBackgroundTask(Runnable task) {
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

}
