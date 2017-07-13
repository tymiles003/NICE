package com.sudicode.nice;

import com.mysql.jdbc.Driver;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class.
 */
public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);

    /**
     * Illegal.
     */
    private Util() {
    }

    /**
     * Run a task in the background. The background thread will have access to the database.
     *
     * @param task A {@link Runnable} containing the task to run
     */
    public static void submitBackgroundTask(Runnable task) {
        Thread t = new Thread(() -> {
            try {
                openDbConnection();
                task.run();
            } finally {
                closeDbConnection();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    /**
     * Opens a new connection and attaches it to the current thread.
     */
    public static void openDbConnection() {
        Base.open(
                Driver.class.getName(),
                String.format("jdbc:mysql://%s/%s"
                        + "?useUnicode=true"
                        + "&nullNamePatternMatchesAll=true"
                        + "&useSSL=false"
                        + "&logger=com.mysql.jdbc.log.Slf4JLogger"
                        + "&profileSQL=true", Constants.DB_SERVER, Constants.DB_NAME),
                Constants.DB_USER,
                Constants.DB_PW);
    }

    /**
     * Closes connection and detaches it from current thread.
     */
    public static void closeDbConnection() {
        Base.close();
        log.info("Closed DB connection.");
    }

}
