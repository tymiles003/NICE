package com.sudicode.nice;

import com.google.common.base.Joiner;
import com.sudicode.nice.database.Course;
import org.h2.Driver;
import org.javalite.activejdbc.Base;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Test utility class.
 */
public class TestUtil {

    /**
     * Opens a new connection and attaches it to the current thread.
     *
     * @throws IOException        if an I/O error occurs
     * @throws SQLException       if a database access error occurs
     * @throws URISyntaxException if this URL is not formatted strictly according to to RFC2396 and cannot be converted to a URI.
     */
    public static void openDbConnection() throws IOException, SQLException, URISyntaxException {
        Base.open(Driver.class.getName(), "jdbc:h2:mem:nicedb;TRACE_LEVEL_FILE=4", "", "");
        String script = Joiner.on("").join(Files.readAllLines(Paths.get(TestUtil.class.getResource("DDL.sql").toURI())));
        for (String sql : script.split(";")) {
            try (Statement stmt = Base.connection().createStatement()) {
                stmt.execute(sql);
            }
        }
    }

    /**
     * Closes connection and detaches it from current thread.
     */
    public static void closeDbConnection() {
        Base.close();
    }

    /**
     * Modifies a {@link Course} to start and end on the given times every day.
     *
     * @param course The course to modify
     * @param start  Start time
     * @param end    End time
     */
    public static void setScheduleForEachDay(Course course, LocalTime start, LocalTime end) {
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            course.setStart(dayOfWeek, start);
            course.setEnd(dayOfWeek, end);
        }
        course.saveIt();
        course.refresh();
    }

}
