package com.sudicode.nice.database;

import com.sudicode.nice.TestUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
        course.setId(counter.incrementAndGet());
        course.insert();
    }

    @Test
    public void testScheduling() throws Exception {
        LocalTime start = LocalDateTime.now().minusHours(1).toLocalTime().truncatedTo(ChronoUnit.SECONDS);
        LocalTime end = LocalDateTime.now().plusHours(1).toLocalTime().truncatedTo(ChronoUnit.SECONDS);
        TestUtil.setScheduleForEachDay(course, start, end);
        assertEquals(start, course.getMondayStart());
        assertEquals(start, course.getTuesdayStart());
        assertEquals(start, course.getWednesdayStart());
        assertEquals(start, course.getThursdayStart());
        assertEquals(start, course.getFridayStart());
        assertEquals(start, course.getSaturdayStart());
        assertEquals(start, course.getSundayStart());
        assertEquals(end, course.getMondayEnd());
        assertEquals(end, course.getTuesdayEnd());
        assertEquals(end, course.getWednesdayEnd());
        assertEquals(end, course.getThursdayEnd());
        assertEquals(end, course.getFridayEnd());
        assertEquals(end, course.getSaturdayEnd());
        assertEquals(end, course.getSundayEnd());
    }

}