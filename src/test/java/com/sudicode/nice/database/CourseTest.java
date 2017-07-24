package com.sudicode.nice.database;

import com.sudicode.nice.TestUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link Course}.
 */
public class CourseTest {

    private static AtomicInteger counter;

    private Course course;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        counter = new AtomicInteger();
        TestUtil.openDbConnection();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        TestUtil.closeDbConnection();
    }

    @Before
    public void setUp() throws Exception {
        course = new Course();
        course.setCrn(counter.incrementAndGet());
        course.insert();
    }

    @Test
    public void testScheduling() throws Exception {
        // Set start/end times
        LocalTime setTime = LocalTime.of(0, 0);
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            setTime = setTime.plusHours(1);
            course.setStart(dayOfWeek, setTime);

            setTime = setTime.plusHours(1);
            course.setEnd(dayOfWeek, setTime);
        }
        course.saveIt();
        course.refresh();

        // Get start/end times
        LocalTime getTime = LocalTime.of(0, 0);
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            getTime = getTime.plusHours(1);
            assertEquals(getTime, course.getStart(dayOfWeek));

            getTime = getTime.plusHours(1);
            assertEquals(getTime, course.getEnd(dayOfWeek));
        }
    }

}
