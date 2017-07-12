package com.sudicode.nice.database;

import com.sudicode.nice.TestUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
        course.setId(counter.incrementAndGet());
        course.insert();
    }

    @Test
    public void testScheduling() throws Exception {
        LocalTime time = LocalTime.of(0, 0);

        course.setMondayStart(time);
        course.setTuesdayStart(time.plusHours(1));
        course.setWednesdayStart(time.plusHours(2));
        course.setThursdayStart(time.plusHours(3));
        course.setFridayStart(time.plusHours(4));
        course.setSaturdayStart(time.plusHours(5));
        course.setSundayStart(time.plusHours(6));
        course.setMondayEnd(time.plusHours(7));
        course.setTuesdayEnd(time.plusHours(8));
        course.setWednesdayEnd(time.plusHours(9));
        course.setThursdayEnd(time.plusHours(10));
        course.setFridayEnd(time.plusHours(11));
        course.setSaturdayEnd(time.plusHours(12));
        course.setSundayEnd(time.plusHours(13));
        course.saveIt();
        course.refresh();

        assertEquals(time, course.getMondayStart());
        assertEquals(time.plusHours(1), course.getTuesdayStart());
        assertEquals(time.plusHours(2), course.getWednesdayStart());
        assertEquals(time.plusHours(3), course.getThursdayStart());
        assertEquals(time.plusHours(4), course.getFridayStart());
        assertEquals(time.plusHours(5), course.getSaturdayStart());
        assertEquals(time.plusHours(6), course.getSundayStart());
        assertEquals(time.plusHours(7), course.getMondayEnd());
        assertEquals(time.plusHours(8), course.getTuesdayEnd());
        assertEquals(time.plusHours(9), course.getWednesdayEnd());
        assertEquals(time.plusHours(10), course.getThursdayEnd());
        assertEquals(time.plusHours(11), course.getFridayEnd());
        assertEquals(time.plusHours(12), course.getSaturdayEnd());
        assertEquals(time.plusHours(13), course.getSundayEnd());
    }

}