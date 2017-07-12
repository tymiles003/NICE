package com.sudicode.nice;

import com.sudicode.nice.database.Course;
import org.javalite.activejdbc.Base;
import org.springframework.core.io.InputStreamResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalTime;

/**
 * Test utility class.
 */
public class TestUtil {

    /**
     * Opens a new connection and attaches it to the current thread.
     */
    public static void openDbConnection() throws IOException, SQLException, URISyntaxException {
        Base.open("org.h2.Driver", "jdbc:h2:mem:db1", "", "");
        ScriptUtils.executeSqlScript(Base.connection(), new InputStreamResource(TestUtil.class.getResourceAsStream("DDL.sql")));
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
        course.setMondayStart(start);
        course.setTuesdayStart(start);
        course.setWednesdayStart(start);
        course.setThursdayStart(start);
        course.setFridayStart(start);
        course.setSaturdayStart(start);
        course.setSundayStart(start);
        course.setMondayEnd(end);
        course.setTuesdayEnd(end);
        course.setWednesdayEnd(end);
        course.setThursdayEnd(end);
        course.setFridayEnd(end);
        course.setSaturdayEnd(end);
        course.setSundayEnd(end);
        course.saveIt();
        course.refresh();
    }

}
